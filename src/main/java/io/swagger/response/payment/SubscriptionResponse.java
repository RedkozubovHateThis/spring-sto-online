package io.swagger.response.payment;

import io.swagger.postgres.model.payment.Subscription;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class SubscriptionResponse {

    private Long id;
    private String name;
    private SubscriptionTypeResponse type;
    private Date startDate;
    private Date endDate;
    private Boolean isRenewable;
    private Double documentCost;
    private Integer documentsCount;
    private Integer documentsRemains;
    private Boolean isClosedEarly;

    public SubscriptionResponse(Subscription subscription, Integer documentsRemains) {
        this.id = subscription.getId();
        this.name = subscription.getName();
        this.type = new SubscriptionTypeResponse( subscription.getType() );
        this.startDate = subscription.getStartDate();
        this.endDate = subscription.getEndDate();
        this.isRenewable = subscription.getIsRenewable();
        this.documentCost = subscription.getDocumentCost();
        this.documentsCount = subscription.getDocumentsCount();
        this.documentsRemains = documentsRemains;
        this.isClosedEarly = subscription.getIsClosedEarly();
    }

}
