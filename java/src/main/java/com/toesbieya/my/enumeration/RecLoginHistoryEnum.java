package com.toesbieya.my.enumeration;

public enum RecLoginHistoryEnum {
    LOGOUT(0), LOGIN(1);

    private int code;

    RecLoginHistoryEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
