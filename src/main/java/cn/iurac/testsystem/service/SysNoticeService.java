package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Repo;
import cn.iurac.testsystem.entity.SysNotice;
import cn.iurac.testsystem.param.request.notice.NoticePageRequestParam;
import cn.iurac.testsystem.param.request.notice.NoticeRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoRequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SysNoticeService extends IService<SysNotice> {

    SysNotice getLatest();

    Page<SysNotice> listForPage(Page<SysNotice> page, NoticePageRequestParam data);

    boolean save(NoticeRequestParam data);
}
