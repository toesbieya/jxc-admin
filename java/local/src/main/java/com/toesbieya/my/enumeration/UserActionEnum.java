package com.toesbieya.my.enumeration;

public enum UserActionEnum {
    FAIL(0), SUCCESS(1);

    private final int code;

    UserActionEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
