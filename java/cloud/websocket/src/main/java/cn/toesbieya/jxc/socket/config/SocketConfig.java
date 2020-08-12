package cn.toesbieya.jxc.socket.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.listener.ExceptionListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@EnableConfigurationProperties(SocketProperty.class)
@DependsOn("redisUtil")
public class SocketConfig {
    private SocketProperty property;

    @Bean
    public SocketIOServer socketIOServer() throws IOException {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();

        setSSL(config);
        config.setHostname(property.getHostname());
        config.setPort(property.getPort());
        config.setMaxFramePayloadLength(property.getMaxFramePayloadLength());
        config.setMaxHttpContentLength(property.getMaxHttpContentLength());
        config.setAddVersionHeader(false);
        config.setExceptionListener(new ExceptionListener() {
            @Override
            public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
            }

            @Override
            public void onDisconnectException(Exception e, SocketIOClient client) {
            }

            @Override
            public void onConnectException(Exception e, SocketIOClient client) {
            }

            @Override
            public void onPingException(Exception e, SocketIOClient client) {
            }

            @Override
            public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
                return false;
            }
        });

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

    private void setSSL(com.corundumstudio.socketio.Configuration config) throws IOException {
        String keyStore = property.getKeyStore();

        if (StringUtils.isEmpty(keyStore)) return;

        InputStream inputStream;
        String classpathPrefix = "classpath:";

        if (keyStore.startsWith(classpathPrefix)) {
            inputStream = new ClassPathResource(keyStore.substring(classpathPrefix.length())).getInputStream();
        }
        else {
            inputStream = new FileInputStream(new File(keyStore));
        }

        config.setKeyStore(inputStream);

        String password = property.getKeyStorePassword();

        if (!StringUtils.isEmpty(password)) {
            config.setKeyStorePassword(password);
        }
    }
}
