package com.blog.blogbackend.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")              // 拦截所有 /api
                .excludePathPatterns(                     // 放行登录
                        "/api/auth/login",
                        "/api/comments",          // 前台评论接口放行
                        "/api/comments/**",       // 前台评论详情放行
                        "/api/hello",
                        "/api/articles",          // 列表放行（游客能看）
                        "/api/articles/*"
                );
    }

    // 顺便开跨域，前端才能调
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
