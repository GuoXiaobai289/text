package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Record;
import cn.iurac.testsystem.entity.Repo;
import cn.iurac.testsystem.exception.ServiceException;
import cn.iurac.testsystem.param.request.record.RecordRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoPageRequestParam;
import cn.iurac.testsystem.param.request.repo.RepoRequestParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface RecordService extends IService<Record> {

    List<Record> list(Record record);

    boolean saveOrUpdate(Long userId, Long examId, List<RecordRequestParam> data) throws ServiceException;
}
