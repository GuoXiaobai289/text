package cn.iurac.testsystem.mapper;

import cn.iurac.testsystem.entity.Exam;
import cn.iurac.testsystem.param.request.exam.ExamPageRequestParam;
import cn.iurac.testsystem.param.response.page.ExamPageResponseParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity cn.iurac.testsystem.entity.Exam
 */
public interface ExamMapper extends BaseMapper<Exam> {

    Page<ExamPageResponseParam> listForPage(@Param("page") Page<ExamPageResponseParam> page, @Param("param") ExamPageRequestParam param);

    Page<ExamPageResponseParam> listExamByClazzIds(@Param("page") Page<ExamPageResponseParam> page, @Param("clazzIds") List<Long> clazzIds);

    List<Long> checkExamPerm(@Param("clazzIds") List<Long> clazzIds);

    Page<ExamPageResponseParam> listAnalysisForPage(@Param("page") Page<ExamPageResponseParam> p, @Param("id") Long id);
}




