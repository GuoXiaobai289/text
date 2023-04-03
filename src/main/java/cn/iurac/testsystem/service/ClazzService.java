package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Clazz;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.param.request.clazz.ClazzPageRequestParam;
import cn.iurac.testsystem.param.request.clazz.ClazzRequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.net.ssl.SSLSession;
import java.util.List;

/**
 *
 */
public interface ClazzService extends IService<Clazz> {

    Page<Clazz> listForPage(Page<Clazz> page, ClazzPageRequestParam data);

    boolean save(ClazzRequestParam data);

    Clazz getByName(String name);

    List<Clazz> list(Clazz clazz);

    List<Clazz> listByTeacherId(Long id);

    List<Clazz> listByExamId(Long id);
}
