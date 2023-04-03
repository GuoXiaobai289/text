package cn.iurac.testsystem.mapper;

import cn.iurac.testsystem.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity cn.iurac.testsystem.entity.User
 */
public interface UserMapper extends BaseMapper<User> {
    List<Long> listByExamId(@Param("examId") Long examId);
}




