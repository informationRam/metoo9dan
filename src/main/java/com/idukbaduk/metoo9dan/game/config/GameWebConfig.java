package com.idukbaduk.metoo9dan.game.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GameWebConfig implements WebMvcConfigurer {
    private String resourcePath = "/upload/game/**"; //view에서 접근할 경로
    private String savePath = "file:///Users/ryuahn/Desktop/baduk/game/"; //실제 파일 저장 경로 윈도우 file:///C:/springboot_img;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourcePath)
                .addResourceLocations(savePath);
    }
}
