package cn.toesbieya.jxc.doc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "cn.toesbieya.jxc")
@MapperScan("cn.toesbieya.jxc.doc.mapper")
public class DocApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocApplication.class, args);
    }
}