package cn.iurac.testsystem.enums;

import cn.hutool.core.util.ObjectUtil;

public enum ExamStateEnum {
    NOTSTART(0),
    LOADING(1),
    FINISH(2),
    END(3);

    private Integer code;

    ExamStateEnum(Integer code) {
        this.code = code;
    }

    public static ExamStateEnum getByCode(Integer code) {
        for (ExamStateEnum eu : values()) {
            if (ObjectUtil.equal(eu.code, code)) {
                return eu;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

}