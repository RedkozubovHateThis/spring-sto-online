package io.swagger.service;

import io.swagger.postgres.model.enums.SubscriptionType;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.response.payment.request.ExtendedResponse;
import io.swagger.response.payment.request.RegisterResponse;

public interface PaymentService {
    RegisterResponse registerPayment(Integer amount, User user) throws PaymentException;
    PaymentResponse updateRequestExtended(String orderId) throws PaymentException;
    PaymentResponse updateRequestExtended(PaymentRecord paymentRecord) throws PaymentException;
    Subscription buySubscription(SubscriptionType subscriptionType, User user) throws PaymentException;
    void buySubscriptionAddon(Long subscriptionId, Integer documentsCount, User user) throws PaymentException;

    void updateRenewalSubscription(SubscriptionType subscriptionType, User user) throws PaymentException;
}
