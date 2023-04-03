package cn.iurac.testsystem.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.SysNotice;
import cn.iurac.testsystem.param.request.notice.NoticePageRequestParam;
import cn.iurac.testsystem.param.request.operation.OperationPageRequestParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.SysOperation;
import cn.iurac.testsystem.service.SysOperationService;
import cn.iurac.testsystem.mapper.SysOperationMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SysOperationServiceImpl extends ServiceImpl<SysOperationMapper, SysOperation>
implements SysOperationService{

    @Override
    public Page<SysOperation> listForPage(Page<SysOperation> page, OperationPageRequestParam param) {
        QueryWrapper<SysOperation> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getOperation()),"operation",param.getOperation())
                .like(StrUtil.isNotBlank(param.getUrl()),"url",param.getUrl())
                .like(StrUtil.isNotBlank(param.getUserName()),"user_name",param.getUserName())
                .like(StrUtil.isNotBlank(param.getIp()),"ip",param.getIp())
                .ge(StrUtil.isNotBlank(param.getStartTime()),"create_time",param.getStartTime())
                .le(StrUtil.isNotBlank(param.getEndTime()),"create_time", param.getEndTime())
                .orderByDesc("create_time");

        return page(page,queryWrapper);
    }
}




