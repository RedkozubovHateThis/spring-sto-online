package io.swagger.postgres.model.payment;

import io.swagger.postgres.model.enums.PaymentState;
import io.swagger.postgres.model.enums.PaymentType;
import io.swagger.postgres.model.security.User;
import io.swagger.response.payment.request.ExtendedResponse;
import io.swagger.response.payment.request.embed.CardAuthInfo;
import io.swagger.response.payment.request.embed.PaymentAmountInfo;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class PaymentRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private String ipAddress;

    private String maskedPan;

    @ManyToOne
    private User user;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private Subscription subscription;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private SubscriptionAddon subscriptionAddon;

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
