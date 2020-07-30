package cn.toesbieya.jxc.socket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties("socket")
@RefreshScope
@Data
public class SocketProperty {
    private String hostname = "localhost";
    private Integer port = 12580;
    private Integer maxFramePayloadLength = 1024 * 1024;
    private Integer maxHttpContentLength = 1024 * 1024;
}
