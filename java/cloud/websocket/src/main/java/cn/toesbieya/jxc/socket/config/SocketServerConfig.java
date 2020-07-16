package cn.toesbieya.jxc.socket.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn("redisUtil")
public class SocketServerConfig {
    private SocketConfig socketConfig;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();

        config.setHostname(socketConfig.getHostname());
        config.setPort(socketConfig.getPort());
        config.setMaxFramePayloadLength(socketConfig.getMaxFramePayloadLength());
        config.setMaxHttpContentLength(socketConfig.getMaxHttpContentLength());
        config.setAddVersionHeader(false);

        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }

    @Autowired
    public void setSocketConfig(SocketConfig socketConfig) {
        this.socketConfig = socketConfig;
    }
}
