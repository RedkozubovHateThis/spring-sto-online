package io.swagger.response.payment;

import io.swagger.postgres.model.enums.SubscriptionType;
import io.swagger.postgres.model.payment.Subscription;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class SubscriptionResponse {

    private Long id;
    private String name;
    private SubscriptionType type;
    private SubscriptionType renewalType;
    private Date startDate;
    private Date endDate;
    private Boolean isRenewable;
    private Double renewalCost;
    private Double documentCost;
    private Integer documentsCount;
    private Integer documentsRemains;

    public SubscriptionResponse(Subscription subscription, Integer documentsRemains) {
        this.id = subscription.getId();
        this.name = subscription.getName();
        this.type = subscription.getType();
        this.renewalType = subscription.getRenewalType();
        this.startDate = subscription.getStartDate();
        this.endDate = subscription.getEndDate();
        this.isRenewable = subscription.getIsRenewable();
        this.renewalCost = subscription.getRenewalCost();
        this.documentCost = subscription.getDocumentCost();
        this.documentsCount = subscription.getDocumentsCount();
        this.documentsRemains = documentsRemains;
    }

}
