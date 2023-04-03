package cn.iurac.testsystem.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.Question;
import cn.iurac.testsystem.entity.QuestionRepo;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.param.request.question.QuestionPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoRequestParam;
import cn.iurac.testsystem.service.QuestionRepoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.Repo;
import cn.iurac.testsystem.service.RepoService;
import cn.iurac.testsystem.mapper.RepoMapper;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service("RepoService")
public class RepoServiceImpl extends ServiceImpl<RepoMapper, Repo>
implements RepoService{

    @Resource
    private QuestionRepoService questionRepoService;

    @Override
    public Page<Repo> listForPage(Page<Repo> page, RepoPageRequestParam param) {
        QueryWrapper<Repo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getTitle()),"title",param.getTitle())
                .ge(StrUtil.isNotBlank(param.getStartTime()),"create_time",param.getStartTime())
                .le(StrUtil.isNotBlank(param.getEndTime()),"create_time", param.getEndTime());

        return page(page,queryWrapper);
    }

    @Override
    public boolean save(RepoRequestParam param) {
        Long id = ((User) SecurityUtils.getSubject().getPrincipal()).getId();
        Repo repo = Repo.builder().createTime(DateUtil.date()).updateTime(DateUtil.date()).createUserId(id).deleteFlag( 0).title(param.getTitle()).build();
        return save(repo);
    }

    @Override
    public List<Repo> listByQuestionId(Long id) {
        QuestionRepo questionRepo = QuestionRepo.builder().questionId(id).build();
        List<QuestionRepo> list = questionRepoService.list(questionRepo);
        List<Long> repoIds = list.stream().map(QuestionRepo::getRepoId).collect(Collectors.toList());
        return repoIds.size()<=0? Lists.newArrayList():listByIds(repoIds);
    }
}




