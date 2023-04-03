package cn.iurac.testsystem.config;

import cn.iurac.testsystem.job.ExamStateHelper;
import cn.iurac.testsystem.service.ExamService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ExamJobConfig implements SmartInitializingSingleton, DisposableBean {

    @Resource
    private ExamService examService;

    @Override
    public void afterSingletonsInstantiated() {
        ExamStateHelper.getInstance().start();
    }

    @Override
    public void destroy() throws Exception {
        ExamStateHelper.getInstance().toStop();
    }
}
