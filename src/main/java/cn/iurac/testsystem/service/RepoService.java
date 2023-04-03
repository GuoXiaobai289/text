package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Question;
import cn.iurac.testsystem.entity.Repo;
import cn.iurac.testsystem.param.request.question.QuestionPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoRequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface RepoService extends IService<Repo> {

    Page<Repo> listForPage(Page<Repo> page, RepoPageRequestParam data);

    boolean save(RepoRequestParam data);

    List<Repo> listByQuestionId(Long id);
}
