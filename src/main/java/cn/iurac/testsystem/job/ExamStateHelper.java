package cn.iurac.testsystem.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iurac.testsystem.entity.Exam;
import cn.iurac.testsystem.enums.ExamStateEnum;
import cn.iurac.testsystem.exception.JobException;
import cn.iurac.testsystem.service.ExamService;
import cn.iurac.testsystem.util.ApplicationContextUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class ExamStateHelper {
    // 饿汉单例模式
    private ExamStateHelper(){};
    private static final ExamStateHelper instance = new ExamStateHelper();
    public static ExamStateHelper getInstance(){
        return instance;
    }

    // 任务状态更新线程池以及相应的监听器
    private ThreadPoolExecutor updateStateThreadPool = null;
    private Thread updateStateMonitorThread;
    private volatile boolean toStop = false;
    // 延迟队列存放即将更新的任务
    private final DelayQueue delayQueue = new DelayQueue<>();
    // 每次监控的时间范围
    public static final Long PER_MONITOR_MS = 30 * 60 * 1000L;

    public void start(){
        updateStateThreadPool = new ThreadPoolExecutor(
                5,
                10,
                1L,
                TimeUnit.SECONDS,
                delayQueue,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r,"考试状态更新线程池—线程"+RandomUtil.randomString(6));
                    }
                }, new ThreadPoolExecutor.CallerRunsPolicy());
        updateStateThreadPool.prestartAllCoreThreads();

        updateStateMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ExamService examService = ApplicationContextUtil.getBean(ExamService.class);
                while (!toStop){
                    Long amendMs = 0L;
                    try {
                        Long start = DateUtil.current(false);
                        List<Exam> endExamList = examService.listWillUpdate(DateUtil.date(), PER_MONITOR_MS, true);
                        for (Exam exam : endExamList) {
                            exam.setState(ExamStateEnum.FINISH.getCode());
                            UpdateStateJob job = new UpdateStateJob(exam, new DateTime(exam.getEndTime()));
                            execute(job);
                        }
                        List<Exam> startExamList = examService.listWillUpdate(DateUtil.date(), PER_MONITOR_MS, false);
                        for (Exam exam : startExamList) {
                            exam.setState(ExamStateEnum.LOADING.getCode());
                            UpdateStateJob job = new UpdateStateJob(exam, new DateTime(exam.getStartTime()));
                            execute(job);
                        }
                        Long end = DateUtil.current(false);
                        amendMs = end - start;
                    }catch (JobException e){
                        log.error("待更新任务列表加入线程池失败");
                        e.printStackTrace();
                        continue;
                    }

                    try {
                        TimeUnit.MILLISECONDS.sleep(PER_MONITOR_MS - amendMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateStateMonitorThread.setDaemon(true);
        updateStateMonitorThread.setName("考试状态更新监控器线程");
        updateStateMonitorThread.start();
    }

    public void toStop(){
        toStop = true;

        updateStateThreadPool.shutdownNow();
        updateStateMonitorThread.interrupt();
        try {
            //当前线程等待检测线程关闭中断结束
            updateStateMonitorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void execute(UpdateStateJob job) throws JobException {
        if(ObjectUtil.isNull(job) || ObjectUtil.isNull(job.getUpdateTime()) || ObjectUtil.isNull(job.getExam())){
            throw new JobException("执行任务消息不能为空");
        }
        log.info("设置一个考试状态更新任务{},预计更新时间在{}",job.getExam().getTitle(),DateUtil.format(job.getUpdateTime(), "yyyy-MM-dd HH:mm"));
        updateStateThreadPool.execute(job);
    }

    @NoArgsConstructor
    @Getter
    public static class UpdateStateJob implements Runnable, Delayed {
        private Exam exam;
        private DateTime updateTime;

        public UpdateStateJob(Exam exam, DateTime updateTime) {
            this.exam = exam;
            this.updateTime = updateTime;
        }

        @SneakyThrows
        @Override
        public void run() {
            ExamService examService = ApplicationContextUtil.getBean(ExamService.class);
            Exam dbExam = examService.getById(exam.getId());
            if(dbExam.getUpdateTime().equals(exam.getUpdateTime())){
                log.info("考试【{}】状态更新,预定时间为:{}",exam.getTitle(),updateTime.toMsStr());
                Date date = DateUtil.date();
                exam.setUpdateTime(date);
                examService.updateById(exam);
                // 某些时间范围小的考试，防止结束状态的修改延迟太久，提前加入线程池中
                if(ObjectUtil.equal(exam.getState(),ExamStateEnum.LOADING.getCode()) && (exam.getEndTime().getTime() < date.getTime() + PER_MONITOR_MS)){
                    Exam tmp = examService.getById(exam.getId());
                    tmp.setState(ExamStateEnum.FINISH.getCode());
                    instance.execute(new ExamStateHelper.UpdateStateJob(tmp, new DateTime(tmp.getEndTime())));
                }
            }else {
                log.info("发现考试【{}】有更新,取消任务(预期updateTime:{},当前updateTime:{})",exam.getTitle(),exam.getUpdateTime(),dbExam.getUpdateTime());
            }
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long now = DateUtil.date().getTime();
            long delay = this.updateTime.getTime() - now;
            return delay;
        }

        @Override
        public int compareTo(Delayed o) {
            UpdateStateJob job = (UpdateStateJob) o;
            long between = this.updateTime.getTime() - job.updateTime.getTime();
            return between>0?1:-1;
        }
    }
}
