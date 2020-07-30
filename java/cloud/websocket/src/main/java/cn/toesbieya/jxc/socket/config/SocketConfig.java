package cn.toesbieya.jxc.socket.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@EnableConfigurationProperties(SocketProperty.class)
@DependsOn("redisUtil")
public class SocketConfig {
    private SocketProperty property;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();

        config.setHostname(property.getHostname());
        config.setPort(property.getPort());
        config.setMaxFramePayloadLength(property.getMaxFramePayloadLength());
        config.setMaxHttpContentLength(property.getMaxHttpContentLength());
        config.setAddVersionHeader(false);

        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }

    @Autowired
    public void setSocketProperty(SocketProperty socketProperty) {
        this.property = socketProperty;
    }
}
