package cn.iurac.testsystem.param.response.chart;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ExamAnalysisChartResponseParam implements Serializable {

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private List<Date> dateList;

    private List<Integer> countList;

}
