package cn.toesbieya.jxc.utils;

import cn.toesbieya.jxc.constant.SocketConstant;
import cn.toesbieya.jxc.model.vo.SocketEventVo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WebSocketUtil {
    //获取在线用户的总数
    public static long getOnlineUserNum() {
        return RedisUtil.scard(SocketConstant.REDIS_ONLINE_USER);
    }

    //获取所有在线用户的id集合
    public static Set<Integer> getOnlineUserIds() {
        Set<Object> result = RedisUtil.smembers(SocketConstant.REDIS_ONLINE_USER);

        return result.stream().map(e -> (Integer) e).collect(Collectors.toSet());
    }

    //发送消息
    public static void sendEvent(SocketEventVo vo) {
        RedisUtil.getRedisTemplate().convertAndSend(SocketConstant.REDIS_EVENT_TOPIC_SEND, vo);
    }

    //登出
    public static void sendLogoutEvent(List<Integer> to, String msg) {
        SocketEventVo vo = new SocketEventVo();
        vo.setEvent(SocketConstant.EVENT_LOGOUT);
        vo.setTo(to);
        vo.setData(msg);
        sendEvent(vo);
    }
}
