package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.ServiceAddonDictionary;
import io.swagger.postgres.model.ServiceWorkDictionary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceAddonDictionaryResourceProcessor extends SimpleResourceProcessor<ServiceAddonDictionary> {

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, ServiceAddonDictionary serviceAddonDictionary) {

    }
}
