package cn.iurac.testsystem.util;

import cn.iurac.testsystem.param.ResponseParam;

public class ResponseUtil {

    public static ResponseParam getErrorResponseParam(Throwable ex) {

        ResponseParam ResponseParam = new ResponseParam(ex.getMessage());
        ResponseParam.getResult().setStatus(0);
        ResponseParam.getResult().setMsg(ex.getMessage());

        return ResponseParam;
    }

    public static ResponseParam getErrorResponseParam(String msg) {

        ResponseParam ResponseParam = new ResponseParam();
        ResponseParam.getResult().setStatus(0);
        ResponseParam.getResult().setMsg(msg);

        return ResponseParam;
    }

    public static ResponseParam getErrorResponseParam() {

        ResponseParam ResponseParam = new ResponseParam();
        ResponseParam.getResult().setStatus(0);

        return ResponseParam;
    }

}
