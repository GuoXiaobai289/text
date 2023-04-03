package cn.iurac.testsystem.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.QuestionRepo;
import cn.iurac.testsystem.service.QuestionRepoService;
import cn.iurac.testsystem.mapper.QuestionRepoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class QuestionRepoServiceImpl extends ServiceImpl<QuestionRepoMapper, QuestionRepo>
implements QuestionRepoService{

    @Override
    public List<QuestionRepo> list(QuestionRepo questionRepo) {
        QueryWrapper<QuestionRepo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(questionRepo.getQuestionId()),"question_id",questionRepo.getQuestionId())
                .eq(ObjectUtil.isNotNull(questionRepo.getRepoId()),"repo_id",questionRepo.getRepoId());
        return list(queryWrapper);
    }
}




