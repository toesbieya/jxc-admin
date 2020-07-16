package com.toesbieya.my.enumeration;

public enum GeneralStatusEnum {
    DISABLED(0), ENABLED(1);

    private final int code;

    GeneralStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
