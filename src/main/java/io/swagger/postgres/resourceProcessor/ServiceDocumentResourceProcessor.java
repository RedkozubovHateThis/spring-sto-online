package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceDocumentResourceProcessor extends SimpleResourceProcessor<ServiceDocument> {

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, ServiceDocument serviceDocument) {

    }
}
