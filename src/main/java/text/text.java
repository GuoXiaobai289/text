import cn.iurac.testsystem.entity.excelentity.ExcelData;
import cn.iurac.testsystem.util.EmployeesListener;
import com.alibaba.excel.EasyExcel;

import java.io.File;

public class text {


    public static void main(String[] args){
        File file = new File("C:\\Users\\gqs_1145511747719778\\Desktop\\ssssss.xlsx");
        String absolutePath = file.getAbsolutePath();
//        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(absolutePath, ExcelData.class, new EmployeesListener()).sheet().doRead();
    }
}
