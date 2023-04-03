package cn.iurac.testsystem.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Objects;

public enum RoleEnum {
    STUDENT(1, "student"),
    TEACHER(2, "teacher"),
    ADMIN(3, "admin"),
    SUPERADMIN(4, "superadmin");

    private Integer code;
    private String role;

    RoleEnum(Integer code, String role) {
        this.code = code;
        this.role = role;
    }

    public static RoleEnum getByCode(Integer code) {
        for (RoleEnum eu : values()) {
            if (ObjectUtil.equal(eu.code, code)) {
                return eu;
            }
        }

        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getRole() {
        return role;
    }
}