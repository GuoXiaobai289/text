package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.QuestionRepo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface QuestionRepoService extends IService<QuestionRepo> {

    List<QuestionRepo> list(QuestionRepo questionRepo);
}
