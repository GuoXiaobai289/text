package cn.iurac.testsystem.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Objects;

public enum QuestionTypeEnum {
    RADIO(1,"单选题"),
    MULTIPLE(2,"多选题"),
    JUDGE(3,"判断题");

    private Integer code;
    private String type;

    QuestionTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public static QuestionTypeEnum getByCode(Integer code) {
        for (QuestionTypeEnum eu : values()) {
            if (ObjectUtil.equal(eu.code, code)) {
                return eu;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}