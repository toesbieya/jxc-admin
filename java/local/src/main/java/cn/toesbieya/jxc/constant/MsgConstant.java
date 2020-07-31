package cn.toesbieya.jxc.constant;

public interface MsgConstant {
    Integer TYPE_NOTICE = 0;         //通知提醒
    Integer TYPE_ANNOUNCEMENT = 1;   //系统公告
    Integer STATUS_DRAFT = 0;        //拟定
    Integer STATUS_PUBLISHED = 1;    //已发布
    Integer STATUS_WITHDREW = 2;     //已撤回
    Integer TO_ALL = 1;              //发送给全体用户
    Integer TO_RANGE = 0;            //已发送给指定用户
}
