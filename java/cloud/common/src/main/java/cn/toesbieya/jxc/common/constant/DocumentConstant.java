package cn.toesbieya.jxc.common.constant;

public interface DocumentConstant {
    //更新单据ID时的redis锁的键
    String UPDATE_DOCUMENTS_LOCK_KEY = "UPDATE_DOCUMENTS";

    //单据ID的redis缓存的键
    String DOCUMENT_TYPE_REDIS_KEY = "documentsID";

    //单据类型
    String[] DOCUMENT_TYPE = {"CGDD", "CGRK", "CGTH", "XSDD", "XSCK", "XSTH"};
}
