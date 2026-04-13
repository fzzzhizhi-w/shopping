package com.shopping.config;

import com.shopping.interceptor.AdminInterceptor;
import com.shopping.interceptor.AuthInterceptor;
import com.shopping.interceptor.MerchantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final MerchantInterceptor merchantInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/products/**",
                        "/api/categories/**",
                        "/api/public/**",
                        "/api/reviews/products/**",
                        // Admin routes are excluded from AuthInterceptor because
                        // AdminInterceptor applies stricter checks (validates token + role=admin)
                        "/api/admin/**",
                        // Merchant routes are excluded from AuthInterceptor because
                        // MerchantInterceptor applies stricter checks (validates token + role=merchant/admin)
                        "/api/merchant/**",
                        // GroupBuy public endpoints
                        "/api/groupbuy/product/**",
                        // GroupBuy detail is public, and other groupbuy endpoints handle auth manually
                        "/api/groupbuy/**"
                );
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");
        registry.addInterceptor(merchantInterceptor)
                .addPathPatterns("/api/merchant/**");
    }
}
