package cn.iurac.testsystem.util;




import cn.iurac.testsystem.entity.excelentity.ExcelData;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

public class EmployeesListener extends AnalysisEventListener<ExcelData> {


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(ExcelData data, AnalysisContext context) {
        System.out.println("解析到一条数据:"+ data.toString());
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
