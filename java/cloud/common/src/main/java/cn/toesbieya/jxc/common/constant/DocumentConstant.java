package cn.toesbieya.jxc.common.constant;

public class DocumentConstant {
    //更新单据ID时的redis锁的键
    public static final String UPDATE_DOCUMENTS_LOCK_KEY = "UPDATE_DOCUMENTS";

    //单据ID的redis缓存的键
    public static final String DOCUMENT_TYPE_REDIS_KEY = "documentsID";

    //单据类型
    public static final String[] DOCUMENT_TYPE = {"CGDD", "CGRK", "CGTH", "XSDD", "XSCK", "XSTH"};
}
