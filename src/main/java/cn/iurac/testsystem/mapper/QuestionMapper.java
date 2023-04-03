package cn.iurac.testsystem.mapper;

import cn.iurac.testsystem.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity cn.iurac.testsystem.entity.Question
 */
public interface QuestionMapper extends BaseMapper<Question> {
    List<Long> listIdByRepoIds(@Param("repoIds") List<Long> repoIds,@Param("code") Integer code);
}




