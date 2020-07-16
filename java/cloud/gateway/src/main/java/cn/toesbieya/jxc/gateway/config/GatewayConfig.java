package cn.toesbieya.jxc.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties("spring.cloud.gateway")
@Data
public class GatewayConfig {
    private Set<String> whitelist = new HashSet<>();

    private String dataId = "jxc-gateway.yml";

    private String group = "DEFAULT_GROUP";
}
