package com.dream.spring.excel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * @author DreamJM
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dream.spring.excel"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(Collections.singletonList(
                        new ParameterBuilder().name("Authorization").description("Authorization Bearer Token")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header").required(false).build()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Interface").version("1.0").build();
    }
}
