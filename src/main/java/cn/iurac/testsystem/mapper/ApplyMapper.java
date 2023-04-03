package cn.iurac.testsystem.mapper;

import cn.iurac.testsystem.entity.Apply;
import cn.iurac.testsystem.param.request.apply.ApplyPageRequestParam;
import cn.iurac.testsystem.param.response.ApplyListResponseParam;
import cn.iurac.testsystem.param.response.page.ApplyPageResponseParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity cn.iurac.testsystem.entity.Answer
 */
public interface ApplyMapper extends BaseMapper<Apply> {

    Page<ApplyListResponseParam> listApply(@Param("page") Page<ApplyListResponseParam> page, @Param("name") String name, @Param("clazzIds") List<Long> clazzIds);

    Page<ApplyPageResponseParam> listForPage(@Param("page") Page<ApplyPageResponseParam> page, @Param("param") ApplyPageRequestParam param);
}




