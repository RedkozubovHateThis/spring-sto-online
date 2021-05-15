package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentRecordResourceProcessor extends SimpleResourceProcessor<PaymentRecord> {

    @Autowired
    private PaymentService paymentService;

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, PaymentRecord paymentRecord) {
        try {
            if ( paymentRecord.isNeedsProcessing() )
                paymentService.updateRequestExtended( paymentRecord );
        }
        catch ( Exception ignored ) {}
    }
}
