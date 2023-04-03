package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Grade;
import cn.iurac.testsystem.entity.Paper;
import cn.iurac.testsystem.entity.Question;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.param.request.question.QuestionPageRequestParam;
import cn.iurac.testsystem.param.request.question.QuestionRequestParam;
import cn.iurac.testsystem.param.response.chart.PieChartResponseParam;
import cn.iurac.testsystem.param.response.QuestionDetailResponseParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface QuestionService extends IService<Question> {

    Page<Question> listForPage(Page<Question> page, QuestionPageRequestParam data);

    boolean save(QuestionRequestParam data) throws ServiceException;

    boolean update(Question question, QuestionRequestParam data) throws ServiceException;

    PieChartResponseParam repoAnalysis();

    PieChartResponseParam typeAnalysis();

    List<QuestionDetailResponseParam> generate(Long id, Paper paper, Long userId, Grade grade) throws ServiceException;

    List<QuestionDetailResponseParam> doGenerate(List<Long> questionIds);
}
