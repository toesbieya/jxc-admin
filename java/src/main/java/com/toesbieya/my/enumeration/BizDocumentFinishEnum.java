package com.toesbieya.my.enumeration;

public enum BizDocumentFinishEnum {
    TO_BE_STARTED(0), UNDERWAY(1), FINISHED(2);

    private int code;

    BizDocumentFinishEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
