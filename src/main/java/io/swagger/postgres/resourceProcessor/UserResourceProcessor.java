package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.security.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserResourceProcessor extends SimpleResourceProcessor<User> {

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, User user) {

    }
}
