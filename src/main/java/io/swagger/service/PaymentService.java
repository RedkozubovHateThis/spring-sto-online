package io.swagger.service;

import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.response.payment.PromisedAvailableResponse;
import io.swagger.response.payment.request.RegisterResponse;

public interface PaymentService {
    RegisterResponse registerPayment(Integer amount, User user) throws PaymentException;
    void registerPromisedPayment(Integer amount, User user) throws PaymentException;
    PromisedAvailableResponse isPromisedAvailable(User user);
    void processPromisedPayments();
    PaymentRecord updateRequestExtended(String orderId) throws PaymentException;
    PaymentRecord updateRequestExtended(PaymentRecord paymentRecord) throws PaymentException;
    Subscription buySubscription(Long subscriptionTypeId, User user) throws PaymentException;
    void unsubscribe(Long subscriptionId, User user) throws PaymentException;

    Long getRemainsDocuments(Profile profile, Subscription subscription, SubscriptionType subscriptionType);
}
