package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.param.request.user.StudentPageRequestParam;
import cn.iurac.testsystem.param.request.user.UserPageRequestParam;
import cn.iurac.testsystem.param.response.chart.ExamAnalysisChartResponseParam;
import cn.iurac.testsystem.param.response.chart.GradeAnalysisChartResponseParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface UserService extends IService<User> {

    boolean existUserByUsername(String username);

    Page<User> listForPage(Page<User> page, UserPageRequestParam param, boolean listAdmin);


    List<User> listUser(Integer... role);

    boolean lockById(Long id);

    User getByUsername(String username);

    List<User> listByUsername(String username);

    Page<User> listStudentForPage(Page<User> page, StudentPageRequestParam data);

    GradeAnalysisChartResponseParam gradeAnalysis(Long id);

    Integer countByRole(Integer code);

    ExamAnalysisChartResponseParam userAnalysis(Integer beginOffset, Integer length);

    GradeAnalysisChartResponseParam avgGradeAnalysis(Long id);

    GradeAnalysisChartResponseParam stateGradeAnalysis(Long id);

    List<Long> listStudentByExamId(Long id);

    boolean checkExamPerm(Long id);
}
