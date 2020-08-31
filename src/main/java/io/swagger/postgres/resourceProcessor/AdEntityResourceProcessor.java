package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.AdEntity;
import io.swagger.postgres.model.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdEntityResourceProcessor extends SimpleResourceProcessor<AdEntity> {

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, AdEntity adEntity) {

    }
}
