package cn.toesbieya.jxc.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = "cn.toesbieya.jxc")
@MapperScan("cn.toesbieya.jxc.schedule.mapper")
public class ScheduleApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ScheduleApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}