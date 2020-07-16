package cn.toesbieya.jxc.document.enumeration;

public enum DocFinishEnum {
    TO_BE_STARTED(0), UNDERWAY(1), FINISHED(2);

    private final int code;

    DocFinishEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
