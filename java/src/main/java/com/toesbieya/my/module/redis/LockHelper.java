package com.toesbieya.my.module.redis;

import java.io.Closeable;

public interface LockHelper extends Closeable {
    String LOCK_LUA_SCRIPT = "return redis.call('set',KEYS[1],ARGV[1],'EX',ARGV[2],'NX')";
    String UNLOCK_LUA_SCRIPT = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return false end";

    @Override
    void close();

    boolean lock();

    void unlock();
}
