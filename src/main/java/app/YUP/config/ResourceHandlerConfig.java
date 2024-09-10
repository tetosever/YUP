package app.YUP.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for resource handlers.
 * This class is responsible for setting up the resource handlers for static resources.
 * It defines resource handlers for the "/style/**", "/script/**", and "/images/**" URL patterns.
 * The resources are served from the classpath under the "/static/style/", "/static/script/", and "/static/images/" directories respectively.
 * It is annotated with @Configuration to indicate that it is a Spring configuration class.
 * It implements the WebMvcConfigurer interface to customize Spring MVC's configuration.
 */
@Configuration
public class ResourceHandlerConfig implements WebMvcConfigurer {

    /**
     * This method is overridden to add resource handlers for serving static resources.
     * It adds resource handlers for the "/style/**", "/script/**", and "/images/**" URL patterns.
     * The resources are served from the classpath under the "/static/style/", "/static/script/", and "/static/images/" directories respectively.
     *
     * @param registry the ResourceHandlerRegistry for registering resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/style/**").addResourceLocations("classpath:/static/style/");
        registry.addResourceHandler("/script/**").addResourceLocations("classpath:/static/script/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
    }
}