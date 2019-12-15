package io.swagger.service;

import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.security.User;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.response.payment.request.ExtendedResponse;
import io.swagger.response.payment.request.RegisterResponse;

public interface PaymentService {
    RegisterResponse registerPayment(Integer amount, User user) throws PaymentException;
    PaymentResponse updateRequestExtended(String orderId) throws PaymentException;
    PaymentResponse updateRequestExtended(PaymentRecord paymentRecord) throws PaymentException;
}
