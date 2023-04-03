package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Answer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface AnswerService extends IService<Answer> {

    List<Answer> listByQuestionId(Long id);

    boolean removeByQuestionId(Long id);
}
