package cn.iurac.testsystem.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.enums.FieldFlagEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.UserClazz;
import cn.iurac.testsystem.service.UserClazzService;
import cn.iurac.testsystem.mapper.UserClazzMapper;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class UserClazzServiceImpl extends ServiceImpl<UserClazzMapper, UserClazz>
implements UserClazzService {

    @Override
    public boolean existRelation(Long userId, Long clazzId) {
        return ObjectUtil.isNotNull(getByRelation(userId,clazzId,true));
    }

    @Override
    public UserClazz getByRelation(Long userId, Long clazzId, boolean filterQuit) {
        QueryWrapper<UserClazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("clazz_id", clazzId);
        if(!filterQuit){
            queryWrapper.in("quit_flag", FieldFlagEnum.DELETE.getCode(),FieldFlagEnum.NORMAL.getCode());
        }else {
            queryWrapper.eq("quit_flag", FieldFlagEnum.NORMAL.getCode());
        }
        return getOne(queryWrapper,false);
    }

    @Override
    public List<UserClazz> list(UserClazz userClazz) {
        QueryWrapper<UserClazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(userClazz.getClazzId()),"clazz_id",userClazz.getClazzId())
                .eq(ObjectUtil.isNotNull(userClazz.getUserId()),"user_id",userClazz.getUserId())
                .eq(ObjectUtil.isNotNull(userClazz.getQuitFlag()),"quit_flag",userClazz.getQuitFlag());
        return list(queryWrapper);
    }

    @Override
    public List<UserClazz> listByClazzIds(List<Long> clazzIds) {
        QueryWrapper<UserClazz> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CollectionUtil.isNotEmpty(clazzIds),"clazz_id",clazzIds);
        return list(queryWrapper);
    }
}




