package com.idukbaduk.metoo9dan.education.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
        @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /ckUpload/** 은 /resources/ckUpload/ 으로 시작하는 uri호출은 /resources/ckUpload/ 경로 하위에 있는 리소스 파일이다 라는 의미입니다.
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("/resources/static/files/");

        //다른 이미지 업로드를 위한 경로
        registry.addResourceHandler("/upload/game/**")
                .addResourceLocations("classpath:/static/files/game/");
     /*   registry.addResourceHandler("/ckUpload/**")
                .addResourceLocations("/resources/ckUpload/");*/

       /* registry.addResourceHandler(resourcePath)
                .addResourceLocations(savePath);*/
    }
}
