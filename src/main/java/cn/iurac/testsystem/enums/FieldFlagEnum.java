package cn.iurac.testsystem.enums;

import java.util.Objects;

public enum FieldFlagEnum {
    // 删除标志
    NORMAL(0),
    DELETE(1),

    // 锁定标志
    LEGAL(0),
    ILLEGAL(1),

    // 做题正确标志
    WRONG(0),
    CORRECT(1),

    // 成绩标志(NORMAL、FINISH)
    ABSENT(-1),
    END(2),

    // 做题记录标志
    EMPTY(0),
    FINISH(1);

    private Integer code;

    FieldFlagEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}