package cn.iurac.testsystem.util;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.iurac.testsystem.exception.UploadException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtil {

    public static void download(HttpServletResponse response,String fileName, Class pojoClass, Collection collection) throws IOException, UploadException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20") + ".xlsx");
            EasyExcel.write(response.getOutputStream(), pojoClass)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("sheet1")
                    .doWrite(collection);
        } catch (Exception e) {
            log.error("成绩导出失败，文件下载时发生异常",e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSONUtil.toJsonStr(map));
        }
    }
}
