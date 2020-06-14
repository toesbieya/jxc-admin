package com.toesbieya.my.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QiniuUtil {
    private static final String ACCESS_KEY = (String) YmlUtil.get("qiniu.access-key");
    private static final String SECRET_KEY = (String) YmlUtil.get("qiniu.secret-key");
    private static final String BUCKET = (String) YmlUtil.get("qiniu.bucket");
    private static final int TOKEN_EXPIRES = (int) YmlUtil.get("qiniu.token-expires");
    private static final String REDIS_CACHE_KEY = (String) YmlUtil.get("qiniu.redis-cache-key");
    private static final Auth AUTH = Auth.create(ACCESS_KEY, SECRET_KEY);
    private static final BucketManager BUCKET_MANAGER = new BucketManager(AUTH, new Configuration(Region.huadong()));

    public static String getToken() {
        String token = (String) RedisUtil.get(REDIS_CACHE_KEY);

        if (token == null) {
            token = AUTH.uploadToken(BUCKET);
            RedisUtil.set(REDIS_CACHE_KEY, token, TOKEN_EXPIRES);
        }

        return token;
    }

    public static void delete(String key) {
        try {
            BUCKET_MANAGER.delete(BUCKET, key);
        } catch (QiniuException e) {
            log.info("七牛云删除单个文件失败,{}", e.getMessage());
        }
    }

    public static void deleteBatch(String... key) {
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(BUCKET, key);
        try {
            BUCKET_MANAGER.batch(batchOperations);
        } catch (QiniuException e) {
            log.info("七牛云批量删除文件失败,{}", e.getMessage());
        }
    }
}
