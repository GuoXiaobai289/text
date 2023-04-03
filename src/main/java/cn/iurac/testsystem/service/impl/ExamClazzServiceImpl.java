package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.Exam;
import cn.iurac.testsystem.entity.QuestionRepo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.ExamClazz;
import cn.iurac.testsystem.service.ExamClazzService;
import cn.iurac.testsystem.mapper.ExamClazzMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Service
public class ExamClazzServiceImpl extends ServiceImpl<ExamClazzMapper, ExamClazz>
implements ExamClazzService{

    @Override
    public List<ExamClazz> list(ExamClazz examClazz) {
        QueryWrapper<ExamClazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(examClazz.getClazzId()),"clazz_id",examClazz.getClazzId())
                .eq(ObjectUtil.isNotNull(examClazz.getExamId()),"exam_id",examClazz.getExamId());
        return list(queryWrapper);
    }

    @Override
    public ExamClazz get(ExamClazz examClazz) {
        QueryWrapper<ExamClazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(examClazz.getClazzId()),"clazz_id",examClazz.getClazzId())
                .eq(ObjectUtil.isNotNull(examClazz.getExamId()),"exam_id",examClazz.getExamId());
        return getOne(queryWrapper);
    }

    @Override
    public Collection<ExamClazz> listByClazzIds(List<Long> clazzIds) {
        if(CollectionUtil.isEmpty(clazzIds)){
            return Collections.emptyList();
        }
        QueryWrapper<ExamClazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("clazz_id",clazzIds);
        return list(queryWrapper);
    }
}




