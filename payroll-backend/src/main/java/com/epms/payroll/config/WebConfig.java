package com.epms.payroll.config;
import org.springframework.context.annotation.Configuration; import org.springframework.web.servlet.config.annotation.*;
@Configuration public class WebConfig implements WebMvcConfigurer {public void addCorsMappings(CorsRegistry r){r.addMapping("/**").allowedOrigins("*").allowedMethods("GET","POST","PUT","DELETE","OPTIONS").allowedHeaders("*");}}
