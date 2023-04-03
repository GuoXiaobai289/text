package cn.iurac.testsystem;

import cn.hutool.core.date.DateUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("cn.iurac.testsystem.mapper")
@EnableSwagger2 // 开启 swagger
@EnableScheduling // 开启定时任务功能
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);
    }
}
