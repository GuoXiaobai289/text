package cn.iurac.testsystem.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseParam implements Serializable {
    private long costTime;
    private ResponseParam.Result result;
    private Object data;

    public ResponseParam() {
        this.result = new ResponseParam.Result(1);
    }

    public ResponseParam(Object data) {
        this();
        this.data = data;
    }

    @Data
    public static class Result {
        private int status;
        private int code;
        private String msg;

        public Result(int status) {
            this.status = status;
        }
    }


}