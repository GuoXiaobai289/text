package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Paper;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.param.request.paper.PaperPageRequestParam;
import cn.iurac.testsystem.param.request.paper.PaperRequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface PaperService extends IService<Paper> {

    Page<Paper> listForPage(Page<Paper> page, PaperPageRequestParam data);

    boolean save(PaperRequestParam data) throws ServiceException;
}
