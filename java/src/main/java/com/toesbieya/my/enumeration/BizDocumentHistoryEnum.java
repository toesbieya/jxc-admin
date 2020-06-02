package com.toesbieya.my.enumeration;

public enum BizDocumentHistoryEnum {
    WITHDRAW(0), COMMIT(1), PASS(2), REJECT(3);

    private final int code;

    BizDocumentHistoryEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
