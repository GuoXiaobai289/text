package cn.iurac.testsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.Answer;
import cn.iurac.testsystem.service.AnswerService;
import cn.iurac.testsystem.mapper.AnswerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service("AnswerService")
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer>
implements AnswerService{

    @Override
    public List<Answer> listByQuestionId(Long id) {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id",id);
        return list(queryWrapper);
    }

    @Override
    public boolean removeByQuestionId(Long id) {
        UpdateWrapper<Answer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("question_id",id);
        return remove(updateWrapper);
    }
}




