package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.Exam;
import cn.iurac.testsystem.entity.ExamClazz;
import cn.iurac.testsystem.entity.Grade;
import cn.iurac.testsystem.mapper.GradeMapper;
import cn.iurac.testsystem.param.request.grade.GradePageRequestParam;
import cn.iurac.testsystem.param.response.GradeExportResponseParam;
import cn.iurac.testsystem.param.response.page.GradePageResponseParam;
import cn.iurac.testsystem.service.GradeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Service("GradeService")
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade>
implements GradeService {

    @Override
    public Page<GradePageResponseParam> listForPage(Page<GradePageResponseParam> page, GradePageRequestParam param) {
        return baseMapper.listForPage(page, param);
    }

    @Override
    public List<Grade> list(Grade grade) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(grade.getUserId()),"user_id",grade.getUserId())
                .eq(ObjectUtil.isNotNull(grade.getExamId()),"exam_id",grade.getExamId())
                .eq(ObjectUtil.isNotNull(grade.getFinishFlag()),"finish_flag",grade.getFinishFlag());
        return list(queryWrapper);
    }

    @Override
    public Integer countByDate(Date today) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("create_time", DateUtil.formatDate(today));
        return count(queryWrapper);
    }

    @Override
    public List<Grade> listByExamIds(List<Long> examIds) {
        if(CollectionUtil.isEmpty(examIds)){
            return Collections.emptyList();
        }
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CollectionUtil.isNotEmpty(examIds),"exam_id",examIds);
        return list(queryWrapper);
    }

    @Override
    public Grade getOne(Grade grade) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(grade.getUserId()),"user_id",grade.getUserId())
                .eq(ObjectUtil.isNotNull(grade.getExamId()),"exam_id",grade.getExamId());
        queryWrapper.last("limit 1");
        return getOne(queryWrapper);
    }

    @Override
    public List<GradeExportResponseParam> export(GradePageRequestParam data) {
        return this.baseMapper.export(data);
    }
}




