package cn.iurac.testsystem.service;

import cn.hutool.core.date.DateTime;
import cn.iurac.testsystem.entity.Exam;
import cn.iurac.testsystem.exception.JobException;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.param.request.exam.ExamPageRequestParam;
import cn.iurac.testsystem.param.request.exam.ExamRequestParam;
import cn.iurac.testsystem.param.response.chart.ExamAnalysisChartResponseParam;
import cn.iurac.testsystem.param.response.page.ExamPageResponseParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface ExamService extends IService<Exam> {

    Page<ExamPageResponseParam> listForPage(Page<ExamPageResponseParam> page, ExamPageRequestParam data);

    boolean save(ExamRequestParam data) throws ServiceException, JobException;

    boolean update(Long id, ExamRequestParam data) throws ServiceException, JobException;

    List<Exam> listWillUpdate(DateTime date, Long perMonitorMs, boolean isEnd);

    Page<ExamPageResponseParam> listExamByUserId(Page<ExamPageResponseParam> p, Long id);

    ExamAnalysisChartResponseParam examAnalysis(Integer beginOffset, Integer length);

    List<Exam> list(Exam exam);

    Page<ExamPageResponseParam> listAnalysisForPage(Page<ExamPageResponseParam> p, Long id);

    List<Exam> listJustFinished();

    boolean existByPaperId(Long id);
}
