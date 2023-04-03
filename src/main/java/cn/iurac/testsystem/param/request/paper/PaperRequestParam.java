package cn.iurac.testsystem.param.request.paper;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class PaperRequestParam implements Serializable {

    @NotBlank(message = "试卷名不能为空")
    private String title;

    @Min(message = "单选题数量为负数", value = 0L)
    private Integer radioCount = 0;

    @Min(message = "单选题分值为负数", value = 0L)
    private Integer radioScore = 0;

    @Min(message = "多选题数量为负数", value = 0L)
    private Integer selectCount = 0;

    @Min(message = "多选题分值为负数", value = 0L)
    private Integer selectScore = 0;

    @Min(message = "判断题数量为负数", value = 0L)
    private Integer judgeCount = 0;

    @Min(message = "判断题分值为负数", value = 0L)
    private Integer judgeScore = 0;

    @NotNull(message = "至少选择一个题库")
    private List<Long> repoIds;

}
