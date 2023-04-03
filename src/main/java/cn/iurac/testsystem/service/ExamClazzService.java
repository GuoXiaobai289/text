package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Exam;
import cn.iurac.testsystem.entity.ExamClazz;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface ExamClazzService extends IService<ExamClazz> {

    List<ExamClazz> list(ExamClazz examClazz);

    ExamClazz get(ExamClazz examClazz);

    Collection<ExamClazz> listByClazzIds(List<Long> clazzIds);
}
