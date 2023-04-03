package cn.iurac.testsystem.param.request.apply;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description
 * @Author caorui
 * @Date 2022/2/11 17:45
 */
@Data
public class ApplyRequestParam {

    private Long studentId;

    @NotNull(message = "找不到教师信息")
    private Long teacherId;

    @NotNull(message = "找不到班级信息")
    private Long clazzId;

    private Integer type;

}
