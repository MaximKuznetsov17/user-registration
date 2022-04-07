package dev.kuznetsov.userregistration.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
@PropertySource("classpath:thymeleaf.properties")
public class ThymeleafConfiguration {
    @Value("${thymeleaf.template.resolver.prefix}")
    private String prefix;
    @Value("${thymeleaf.template.resolver.suffix}")
    private String suffix;
    @Value("${thymeleaf.template.resolver.mode}")
    private String mode;

    @Bean
    public ClassLoaderTemplateResolver classLoaderTemplateResolver() {
        return new ClassLoaderTemplateResolver();
    }

    @Bean
    public ITemplateResolver thymeleafTemplateResolver(ClassLoaderTemplateResolver templateResolver) {
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(suffix);
        templateResolver.setTemplateMode(mode);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver thymeleafTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver);
        return templateEngine;
    }
}
