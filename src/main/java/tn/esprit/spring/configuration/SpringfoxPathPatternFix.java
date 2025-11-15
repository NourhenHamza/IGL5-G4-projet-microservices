package tn.esprit.spring.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.List;

@Configuration
public class SpringfoxPathPatternFix {

    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    WebMvcRequestHandlerProvider provider = (WebMvcRequestHandlerProvider) bean;
                    try {
                        Field field = WebMvcRequestHandlerProvider.class.getDeclaredField("handlerMappings");
                        field.setAccessible(true);
                        List<?> handlerMappings = (List<?>) field.get(provider);
                        handlerMappings.removeIf(mapping -> mapping.getClass().getName().contains("PatternParser"));
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
                return bean;
            }
        };
    }
}
