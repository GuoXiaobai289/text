package cn.iurac.testsystem.param.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GradeExportResponseParam implements Serializable {

    @ColumnWidth(10)
    @ExcelProperty("学生用户名")
    private String username;

    @ColumnWidth(10)
    @ExcelProperty("姓名")
    private String name;

    @ColumnWidth(30)
    @ExcelProperty("考试标题")
    private String examName;

    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("参加时间")
    private Date attendTime;

    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("完成时间")
    private Date finishTime;

    @ColumnWidth(8)
    @NumberFormat("#.00")
    @ExcelProperty(value = "分数")
    private String userScore;

    @ExcelIgnore
    private Integer finishFlag;

    @ColumnWidth(10)
    @ExcelProperty(value = "完成状态")
    private String finishString;

    @ColumnWidth(20)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("批卷时间")
    private Date createTime;
}
