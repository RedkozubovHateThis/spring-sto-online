package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SubscriptionTypeResourceProcessor extends SimpleResourceProcessor<SubscriptionType> {

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, SubscriptionType subscriptionType) {

    }
}
