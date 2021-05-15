package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.ServiceWorkDictionary;
import io.swagger.postgres.model.VehicleDictionary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceWorkDictionaryResourceProcessor extends SimpleResourceProcessor<ServiceWorkDictionary> {

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, ServiceWorkDictionary serviceWorkDictionary) {

    }
}
