package com.toesbieya.my.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.toesbieya.my.constant.SocketConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(SocketConstant.HOSTNAME);
        config.setPort(SocketConstant.PORT);
        config.setMaxFramePayloadLength(SocketConstant.MAX_FRAME_PAYLOAD);
        config.setMaxHttpContentLength(SocketConstant.MAX_HTTP_CONTENT);
        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
