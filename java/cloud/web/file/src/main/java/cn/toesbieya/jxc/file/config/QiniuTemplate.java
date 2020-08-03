package cn.toesbieya.jxc.file.config;

import cn.toesbieya.jxc.common.util.RedisUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QiniuTemplate {
    private final QiniuProperty property;
    private final Auth auth;
    private final BucketManager bucketManager;

    public QiniuTemplate(QiniuProperty property) {
        this.property = property;
        this.auth = Auth.create(property.getAccessKey(), property.getSecretKey());
        this.bucketManager = new BucketManager(this.auth, new Configuration(property.getRegion().getRegion()));
    }

    public String getToken() {
        String key = property.getRedisCacheKey();

        String token = (String) RedisUtil.get(key);

        if (token == null) {
            token = auth.uploadToken(property.getBucket());
            RedisUtil.set(key, token, property.getTokenExpire());
        }

        return token;
    }

    public void delete(String key) {
        try {
            bucketManager.delete(property.getBucket(), key);
        }
        catch (QiniuException e) {
            log.info("七牛云删除单个文件失败,{}", e.getMessage());
        }
    }

    public void deleteBatch(String... key) {
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(property.getBucket(), key);
        try {
            bucketManager.batch(batchOperations);
        }
        catch (QiniuException e) {
            log.info("七牛云批量删除文件失败,{}", e.getMessage());
        }
    }
}
