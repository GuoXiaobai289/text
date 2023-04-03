package cn.iurac.testsystem.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iurac.testsystem.entity.Repo;
import cn.iurac.testsystem.entity.User;
import cn.iurac.testsystem.param.request.notice.NoticePageRequestParam;
import cn.iurac.testsystem.param.request.notice.NoticeRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoRequestParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.iurac.testsystem.entity.SysNotice;
import cn.iurac.testsystem.service.SysNoticeService;
import cn.iurac.testsystem.mapper.SysNoticeMapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("SysNoticeService")
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice>
implements SysNoticeService{

    @Override
    public SysNotice getLatest() {
        QueryWrapper<SysNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time").last("LIMIT 1");
        return list(queryWrapper).get(0);
    }


    @Override
    public Page<SysNotice> listForPage(Page<SysNotice> page, NoticePageRequestParam param) {
        QueryWrapper<SysNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(param.getTitle()),"title",param.getTitle())
                .like(StrUtil.isNotBlank(param.getContent()),"content",param.getContent())
                .ge(StrUtil.isNotBlank(param.getStartTime()),"create_time",param.getStartTime())
                .le(StrUtil.isNotBlank(param.getEndTime()),"create_time", param.getEndTime());

        return page(page,queryWrapper);
    }

    @Override
    public boolean save(NoticeRequestParam param) {
        Long id = ((User) SecurityUtils.getSubject().getPrincipal()).getId();
        SysNotice sysNotice = SysNotice.builder()
                .createTime(DateUtil.date())
                .updateTime(DateUtil.date())
                .createUserId(id)
                .deleteFlag(0)
                .title(param.getTitle())
                .content(param.getContent())
                .build();
        return save(sysNotice);
    }
}




