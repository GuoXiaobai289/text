package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.UserClazz;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface UserClazzService extends IService<UserClazz> {

    boolean existRelation(Long userId, Long clazzId);

    UserClazz getByRelation(Long userId, Long clazzId, boolean filterDeleted);

    List<UserClazz> list(UserClazz userClazz);

    List<UserClazz> listByClazzIds(List<Long> clazzIds);
}
