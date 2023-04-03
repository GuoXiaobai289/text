package cn.iurac.testsystem.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iurac.testsystem.entity.Apply;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.entity.UserClazz;
import cn.iurac.testsystem.enums.ApplyTypeEnum;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.mapper.ApplyMapper;
import cn.iurac.testsystem.param.request.apply.ApplyPageRequestParam;
import cn.iurac.testsystem.param.request.apply.ApplyRequestParam;
import cn.iurac.testsystem.param.response.ApplyListResponseParam;
import cn.iurac.testsystem.param.response.page.ApplyPageResponseParam;
import cn.iurac.testsystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service("ApplyService")
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply>
implements ApplyService {

    @Resource
    private UserService userService;
    @Resource
    private ClazzService clazzService;
    @Resource
    private UserClazzService userClazzService;

    @Override
    public Page<ApplyPageResponseParam> listForPage(Page<ApplyPageResponseParam> page, ApplyPageRequestParam param) {
        return this.baseMapper.listForPage(page, param);
    }

    @Override
    public boolean save(ApplyRequestParam data) throws ServiceException {
        if(userClazzService.existRelation(data.getTeacherId(),data.getClazzId())){
            QueryWrapper<Apply> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id",data.getStudentId())
                    .eq("teacher_id",data.getTeacherId())
                    .eq("clazz_id",data.getClazzId());
            Apply apply = getOne(queryWrapper);
            UserClazz relation = userClazzService.getByRelation(data.getStudentId(), data.getClazzId(),true);
            if(ObjectUtil.isNotNull(apply) || ObjectUtil.isNotNull(relation)) {
                throw new ServiceException("不允许重复申请");
            }
            apply = Apply.builder()
                    .studentId(data.getStudentId())
                    .teacherId(data.getTeacherId())
                    .clazzId(data.getClazzId())
                    .applyTime(DateUtil.date())
                    .type(ApplyTypeEnum.LOADING.getCode())
                    .deleteFlag(FieldFlagEnum.NORMAL.getCode())
                    .build();
            return save(apply);
        }else {
            throw new ServiceException("该班级里没有相应教师");
        }
    }

    @Override
    @Transactional(rollbackFor = {ServiceException.class,Exception.class})
    public boolean update(Long id, ApplyRequestParam data) throws ServiceException {
        if(userClazzService.existRelation(data.getTeacherId(),data.getClazzId())){
            ApplyTypeEnum applyTypeEnum = ApplyTypeEnum.getByCode(data.getType());
            Apply apply = getById(id);
            apply.setType(applyTypeEnum.getCode());
            if(ObjectUtil.equal(applyTypeEnum, ApplyTypeEnum.REJECT) || ObjectUtil.equal(applyTypeEnum, ApplyTypeEnum.PASS)){
                apply.setFinishTime(DateUtil.date());
                UserClazz userClazz = ObjectUtil.defaultIfNull(userClazzService.getByRelation(apply.getStudentId(), apply.getClazzId(),false), UserClazz.builder().build());
                userClazz.setUserId(apply.getStudentId());
                userClazz.setClazzId(apply.getClazzId());
                userClazz.setQuitFlag(BooleanUtil.toInt(ObjectUtil.notEqual(applyTypeEnum, ApplyTypeEnum.PASS)));
                userClazzService.saveOrUpdate(userClazz);
            }else {
                apply.setFinishTime(null);
            }
            return updateById(apply);
        }else {
            throw new ServiceException("该班级里没有相应教师");
        }
    }

    @Override
    public Page<ApplyListResponseParam> listApply(Page<ApplyListResponseParam> page, String name) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Apply apply = Apply.builder().studentId(user.getId()).build();
        List<Long> clazzIds = list(apply).stream().map(Apply::getClazzId).collect(Collectors.toList());
        return baseMapper.listApply(page, name, clazzIds);
    }

    @Override
    public List<Apply> list(Apply apply) {
        QueryWrapper<Apply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(apply.getStudentId()),"student_id",apply.getStudentId())
                .eq("delete_flag",FieldFlagEnum.NORMAL);
        return list(queryWrapper);
    }
}




