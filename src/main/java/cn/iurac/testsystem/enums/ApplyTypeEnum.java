package cn.iurac.testsystem.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Objects;

public enum ApplyTypeEnum {
    LOADING(0),
    REJECT(-1),
    PASS(1);

    private Integer code;

    ApplyTypeEnum(Integer code) {
        this.code = code;
    }

    public static ApplyTypeEnum getByCode(Integer code) {
        for (ApplyTypeEnum eu : values()) {
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