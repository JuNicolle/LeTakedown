package com.barapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final BarmakerInterceptor barmakerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(barmakerInterceptor)
                .addPathPatterns(
                        "/api/cocktails", "/api/cocktails/**",
                        "/api/categories", "/api/categories/**",
                        "/api/ingredients", "/api/ingredients/**",
                        "/api/commandes", "/api/commandes/**",
                        "/api/utilisateurs/barmaker/register"
                )
                .excludePathPatterns(
                        "/api/utilisateurs/client",
                        "/api/utilisateurs/barmaker/login"
                );
    }
}
