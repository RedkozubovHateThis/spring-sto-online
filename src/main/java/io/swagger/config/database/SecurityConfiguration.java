package io.swagger.config.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;

@Configuration
class SecurityConfiguration {

    @Bean
    EvaluationContextExtension securityExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
