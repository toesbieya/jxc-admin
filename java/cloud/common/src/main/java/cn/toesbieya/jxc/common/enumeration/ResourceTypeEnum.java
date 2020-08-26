package cn.toesbieya.jxc.common.enumeration;

public enum ResourceTypeEnum {
    ROOT(0), FOLDER(1), LEAF(2), API(3);

    private final int code;

    ResourceTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
