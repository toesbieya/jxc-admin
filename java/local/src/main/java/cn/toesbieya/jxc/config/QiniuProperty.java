package cn.toesbieya.jxc.config;

import com.qiniu.storage.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("qiniu")
public class QiniuProperty {
    private String accessKey;

    private String secretKey;

    private String bucket;

    //机房区域，默认为自动
    private QiniuRegion region = QiniuRegion.auto;

    //临时上传凭证的有效期，单位秒
    private Integer tokenExpire = 3600;

    //临时上传凭证的redis键名
    private String redisCacheKey = "qiniu-token";

    enum QiniuRegion {
        auto(Region.autoRegion()),
        huadong(Region.huadong()),
        huabei(Region.huabei()),
        huanan(Region.huanan()),
        beimei(Region.beimei()),
        xinjiapo(Region.xinjiapo());

        private final Region region;

        QiniuRegion(Region region) {
            this.region = region;
        }

        public Region getRegion() {
            return this.region;
        }
    }
}
