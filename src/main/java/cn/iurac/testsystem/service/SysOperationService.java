package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.SysNotice;
import cn.iurac.testsystem.entity.SysOperation;
import cn.iurac.testsystem.param.request.operation.OperationPageRequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SysOperationService extends IService<SysOperation> {
    Page<SysOperation> listForPage(Page<SysOperation> page, OperationPageRequestParam data);
}
