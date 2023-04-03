package cn.iurac.testsystem.param.response.chart;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class PieChartResponseParam implements Serializable {

    private int total;

    private List<Map<String ,Object>> dataList;

}
