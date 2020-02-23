package com.toesbieya.my.listener;

import com.toesbieya.my.model.entity.SysUser;
import com.toesbieya.my.module.SocketModule;
import com.toesbieya.my.utils.Util;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class RedisSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        SysUser sysUser = Util.getUser(se.getSession());
        if (sysUser != null) {
            SocketModule.logout(sysUser.getId(), "登陆状态过期，请重新登陆");
        }
    }
}
