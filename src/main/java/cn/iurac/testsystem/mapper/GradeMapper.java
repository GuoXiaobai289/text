package cn.iurac.testsystem.mapper;

import cn.iurac.testsystem.entity.Grade;
import cn.iurac.testsystem.param.request.grade.GradePageRequestParam;
import cn.iurac.testsystem.param.response.GradeExportResponseParam;
import cn.iurac.testsystem.param.response.page.GradePageResponseParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity cn.iurac.testsystem.entity.Repo
 */
public interface GradeMapper extends BaseMapper<Grade> {

    Page<GradePageResponseParam> listForPage( @Param("page") Page<GradePageResponseParam> page, @Param("param") GradePageRequestParam param);

    List<GradeExportResponseParam> export(@Param("param") GradePageRequestParam param);
}




