package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.SubscriptionOption;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SubscriptionResourceProcessor extends SimpleResourceProcessor<Subscription> {

    @Autowired
    private PaymentService paymentService;

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, Subscription subscription) {
        SubscriptionType subscriptionType = subscription.getType();
        if ( subscriptionType == null
                || ( subscriptionType.getSubscriptionOption() != null && !subscriptionType.getSubscriptionOption().equals( SubscriptionOption.OPERATOR ) ) ) return;

        User user = subscription.getUser();
        if ( user == null ) return;

        Profile profile = user.getProfile();
        if ( profile == null ) return;

        attributes.put( "remains", paymentService.getRemainsDocuments( profile, subscription, subscriptionType ) );
    }
}
