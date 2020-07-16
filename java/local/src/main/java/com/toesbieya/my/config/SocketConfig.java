package com.toesbieya.my.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("socket")
@Data
public class SocketConfig {
    private String hostname = "localhost";
    private Integer port = 12580;
    private Integer maxFramePayloadLength = 1024 * 1024;
    private Integer maxHttpContentLength = 1024 * 1024;
}
