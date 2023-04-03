package cn.iurac.testsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    //配置Swagger的Docket实例
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
//                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("cn.iurac.testsystem.controller"))
                .build();

    }

    //配置Swagger的信息
    @Bean
    public ApiInfo apiInfo(){
        //作者信息
        Contact contact = new Contact("Iurac", "https://www.gitee.com/iurac", "982075965@qq.com");
        return new ApiInfo("JAVA在线考试系统",
                "依靠Swagger搭建出JAVA在线考试系统的API文档",
                "1.0",
                "https://www.gitee.com/iurac",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }
}
