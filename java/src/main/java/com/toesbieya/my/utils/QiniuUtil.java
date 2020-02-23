package com.toesbieya.my.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QiniuUtil {
    private static String ACCESS_KEY = (String) YmlUtil.get("qiniu.access-key");
    private static String SECRET_KEY = (String) YmlUtil.get("qiniu.secret-key");
    private static String BUCKET = (String) YmlUtil.get("qiniu.bucket");
    private static Auth AUTH = Auth.create(ACCESS_KEY, SECRET_KEY);
    private static BucketManager BUCKET_MANAGER = new BucketManager(AUTH, new Configuration(Region.huadong()));

    public static String getToken() {
        return AUTH.uploadToken(BUCKET);
    }

    public static void delete(String key) {
        try {
            BUCKET_MANAGER.delete(BUCKET, key);
        } catch (QiniuException e) {
            log.error("七牛云删除单个文件失败,{}", e.getMessage());
        }
    }

    public static void deleteBatch(String... key) {
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(BUCKET, key);
        try {
            BUCKET_MANAGER.batch(batchOperations);
        } catch (QiniuException e) {
            log.error("七牛云批量删除文件失败,{}", e.getMessage());
        }
    }
}
