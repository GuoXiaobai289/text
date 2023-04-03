package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.*;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.param.request.clazz.ClazzPageRequestParam;
import cn.iurac.testsystem.param.request.clazz.ClazzRequestParam;
import cn.iurac.testsystem.param.request.clazz.ClazzPageRequestParam;
import cn.iurac.testsystem.param.request.clazz.ClazzRequestParam;
import cn.iurac.testsystem.service.ExamClazzService;
import cn.iurac.testsystem.service.UserClazzService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.Clazz;
import cn.iurac.testsystem.service.ClazzService;
import cn.iurac.testsystem.mapper.ClazzMapper;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.SSLSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service("ClazzService")
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz>
implements ClazzService{

    @Resource
    private UserClazzService userClazzService;
    @Resource
    private ExamClazzService examClazzService;

    @Override
    public Page<Clazz> listForPage(Page<Clazz> page, ClazzPageRequestParam param) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getName()),"name",param.getName())
                .ge(StrUtil.isNotBlank(param.getStartTime()),"create_time",param.getStartTime())
                .le(StrUtil.isNotBlank(param.getEndTime()),"create_time", param.getEndTime())
                .in(CollectionUtil.isNotEmpty(param.getClazzIds()),"id", param.getClazzIds());

        return page(page,queryWrapper);
    }

    @Override
    public boolean save(ClazzRequestParam param) {
        Long id = ((User) SecurityUtils.getSubject().getPrincipal()).getId();
        Clazz clazz = Clazz.builder().createTime(DateUtil.date()).updateTime(DateUtil.date()).createUserId(id).deleteFlag( 0).name(param.getName()).build();
        save(clazz);
        UserClazz userClazz = UserClazz.builder().userId(id).clazzId(clazz.getId()).QuitFlag(FieldFlagEnum.NORMAL.getCode()).build();
        return userClazzService.save(userClazz);
    }

    @Override
    public Clazz getByName(String name) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",name);
        return getOne(queryWrapper);
    }

    @Override
    public List<Clazz> list(Clazz clazz) {
        QueryWrapper<Clazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(clazz.getName()),"name",clazz.getName());
        return list(queryWrapper);
    }

    @Override
    public List<Clazz> listByTeacherId(Long id) {
        UserClazz userClazz = UserClazz.builder().userId(id).build();
        List<Long> clazzIds = userClazzService.list(userClazz).stream().map(UserClazz::getClazzId).collect(Collectors.toList());
        return listByIds(clazzIds);
    }

    @Override
    public List<Clazz> listByExamId(Long id) {
        ExamClazz examClazz = ExamClazz.builder().examId(id).build();
        List<Long> clazzIds = examClazzService.list(examClazz).stream().map(ExamClazz::getClazzId).collect(Collectors.toList());
        return listByIds(clazzIds);
    }
}




