package com.neerjaphysio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect /login to /login.html (static file)
        registry.addRedirectViewController("/login", "/login.html");
        
        // Redirect root to login
        registry.addRedirectViewController("/", "/login.html");
        
        // Redirect /home to /index.html (static file)
        registry.addRedirectViewController("/home", "/index.html");
    }
}
