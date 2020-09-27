package io.swagger.postgres.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.BaseEntity;
import io.swagger.postgres.model.enums.PaymentState;
import io.swagger.postgres.model.enums.PaymentType;
import io.swagger.postgres.model.security.User;
import io.swagger.response.payment.request.ExtendedResponse;
import io.swagger.response.payment.request.embed.CardAuthInfo;
import io.swagger.response.payment.request.embed.PaymentAmountInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Data
@JsonApiResource(type = "paymentRecord", resourcePath = "paymentRecords", deletable = false, postable = false)
public class PaymentRecord extends BaseEntity implements Serializable {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(unique = true)
    private String orderId;
    @Column(unique = true, nullable = false)
    private String orderNumber;
    private Integer orderStatus;

    private Integer actionCode;
    @Column(columnDefinition = "TEXT")
    private String actionCodeDescription;

    private Integer errorCode;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private String currency;
    @Column(nullable = false)
    private Integer amount;
    private Integer depositedAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;

    @Column(nullable = false)
    private Date createDate;
    private Date registerDate;
    private Date expirationDate;
    private Boolean isExpired;

    private String ipAddress;

    private String maskedPan;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private User user;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Subscription subscription;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private SubscriptionAddon subscriptionAddon;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private PaymentRecord promisedRecord;

    @OneToOne(mappedBy = "promisedRecord")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private PaymentRecord paymentRecord;

    public void updateRecord(ExtendedResponse extendedResponse) {

        PaymentAmountInfo paymentAmountInfo = extendedResponse.getPaymentAmountInfo();
        if ( paymentAmountInfo != null ) {
            this.depositedAmount = paymentAmountInfo.getDepositedAmount();
            this.paymentState = paymentAmountInfo.getPaymentState();
        }

        CardAuthInfo cardAuthInfo = extendedResponse.getCardAuthInfo();
        if ( cardAuthInfo != null )
            this.maskedPan = cardAuthInfo.getMaskedPan();

        this.orderStatus = extendedResponse.getOrderStatus();
        this.actionCode = extendedResponse.getActionCode();
        this.actionCodeDescription = extendedResponse.getActionCodeDescription();
        this.errorCode = extendedResponse.getErrorCode();
        this.errorMessage = extendedResponse.getErrorMessage();
        this.currency = extendedResponse.getCurrency();
        this.ipAddress = extendedResponse.getIp();

        if ( extendedResponse.getDate() != null )
            this.setRegisterDate( new Date( extendedResponse.getDate() ) );

    }

    public Boolean isPreProcessed() {
        return paymentType.equals( PaymentType.DEPOSIT ) && orderId != null && !paymentState.equals( PaymentState.CREATED );
    }

    public Boolean isProcessed() {
        return paymentType.equals( PaymentType.DEPOSIT ) && !paymentState.equals( PaymentState.CREATED ) && !paymentState.equals( PaymentState.APPROVED );
    }

    public Boolean isNeedsProcessing() {
        return paymentType.equals( PaymentType.DEPOSIT ) && orderId != null && paymentState.equals( PaymentState.APPROVED );
    }

}
