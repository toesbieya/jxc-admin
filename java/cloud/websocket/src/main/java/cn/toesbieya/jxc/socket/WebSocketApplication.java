package cn.toesbieya.jxc.socket;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = "cn.toesbieya.jxc")
public class WebSocketApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(WebSocketApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}