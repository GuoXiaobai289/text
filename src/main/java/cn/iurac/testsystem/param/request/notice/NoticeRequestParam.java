package cn.iurac.testsystem.param.request.notice;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class NoticeRequestParam implements Serializable {

    @NotBlank(message = "题库名不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

}
