package cn.iurac.testsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("cn.iurac.testsystem.mapper")
@EnableSwagger2 // 开启 swagger
@EnableScheduling // 开启定时任务功能
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class TextSystemMain {

    public static void main(String[] args) {
        SpringApplication.run(TextSystemMain.class,args);
    }
}
