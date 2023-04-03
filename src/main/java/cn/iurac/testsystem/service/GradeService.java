package cn.iurac.testsystem.service;

import cn.iurac.testsystem.entity.Grade;
import cn.iurac.testsystem.param.request.grade.GradePageRequestParam;
import cn.iurac.testsystem.param.response.GradeExportResponseParam;
import cn.iurac.testsystem.param.response.page.GradePageResponseParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 *
 */
public interface GradeService extends IService<Grade> {

    Page<GradePageResponseParam> listForPage(Page<GradePageResponseParam> page, GradePageRequestParam data);

    List<Grade> list(Grade grade);

    Integer countByDate(Date cur);

    List<Grade> listByExamIds(List<Long> examIds);

    Grade getOne(Grade grade);

    List<GradeExportResponseParam> export(GradePageRequestParam data);
}
