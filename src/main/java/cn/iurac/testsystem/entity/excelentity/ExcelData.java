package cn.iurac.testsystem.entity.excelentity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelData {
    @ExcelProperty(index = 0)
    private String name;
    @ExcelProperty(index = 1)
    private String phone;
    @ExcelProperty(index = 2)
    private String cla;
}
