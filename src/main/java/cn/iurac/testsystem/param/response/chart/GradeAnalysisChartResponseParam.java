package cn.iurac.testsystem.param.response.chart;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class GradeAnalysisChartResponseParam implements Serializable {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private List<Date> dateList;

    private List<String> examList;

    private List<Integer> scoreList;

    private Map<String, List<Long>> scoreStateMap;

}
