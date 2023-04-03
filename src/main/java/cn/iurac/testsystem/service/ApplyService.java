package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Apply;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.param.request.apply.ApplyPageRequestParam;
import cn.iurac.testsystem.param.request.apply.ApplyRequestParam;
import cn.iurac.testsystem.param.response.ApplyListResponseParam;
import cn.iurac.testsystem.param.response.page.ApplyPageResponseParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface ApplyService extends IService<Apply> {

    Page<ApplyPageResponseParam> listForPage(Page<ApplyPageResponseParam> page, ApplyPageRequestParam data);

    boolean save(ApplyRequestParam data) throws ServiceException;

    boolean update(Long id, ApplyRequestParam data) throws ServiceException;

    List<Apply> list(Apply apply);

    Page<ApplyListResponseParam> listApply(Page<ApplyListResponseParam> p, String name);
}
