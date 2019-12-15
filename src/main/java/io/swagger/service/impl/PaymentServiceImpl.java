package io.swagger.service.impl;

import io.swagger.controller.WebSocketController;
import io.swagger.postgres.model.enums.PaymentState;
import io.swagger.postgres.model.enums.PaymentType;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.PaymentRecordRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.response.payment.request.ExtendedResponse;
import io.swagger.response.payment.request.RegisterResponse;
import io.swagger.response.payment.request.embed.Attribute;
import io.swagger.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final static Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;
    @Autowired
    private WebSocketController webSocketController;
    @Autowired
    private UserRepository userRepository;

    @Value("${sb.api.username}")
    private String sbUsername;
    @Value("${sb.api.password}")
    private String sbPassword;
    @Value("${sb.api.registerUrl}")
    private String sbRegisterUrl;
    @Value("${sb.api.orderStatusExtendedUrl}")
    private String sbOrderStatusExtendedUrl;
    @Value("${sb.api.returnUrl}")
    private String sbReturnUrl;
    @Value("${sb.api.failUrl}")
    private String sbFailUrl;

    @Override
    public synchronized RegisterResponse registerPayment(Integer amount, User user) throws PaymentException {

        try {

            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setPaymentState( PaymentState.CREATED );
            paymentRecord.setAmount( amount );
            paymentRecord.setCreateDate( new Date() );
            paymentRecord.setOrderNumber( generateDepositOrderNumber() );
            paymentRecord.setUser( user );
            paymentRecord.setPaymentType( PaymentType.DEPOSIT );

            paymentRecordRepository.save( paymentRecord );

            RegisterResponse registerResponse = sendRegisterRequest( amount, paymentRecord.getOrderNumber() );

            updateRequest( paymentRecord, registerResponse.getOrderId() );

            return registerResponse;

        }
        catch(Exception e) {
            logger.error("Error during registering request, reason: {}", e .getMessage());
            e.printStackTrace();

            return null;
        }

    }

    private RegisterResponse sendRegisterRequest(Integer amount, String orderNumber) throws PaymentException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("userName", sbUsername);
        map.add("password", sbPassword);
        map.add("orderNumber", orderNumber);
        map.add("amount", amount.toString());
        map.add("returnUrl", sbReturnUrl);
        map.add("failUrl", sbFailUrl);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<RegisterResponse> response = restTemplate.exchange(sbRegisterUrl, HttpMethod.POST, entity, RegisterResponse.class);

        if ( response.getStatusCodeValue() == 200 ) {
            RegisterResponse registerResponse = response.getBody();

            if ( registerResponse != null ) {
                logger.info( "Got orderId: {}, formUrl: {}", registerResponse.getOrderId(), registerResponse.getFormUrl() );
                return registerResponse;
            }
            else
                throw new PaymentException("Ошибка регистрации запроса на оплату на стороне банка!");
        }
        else
            throw new PaymentException("Ошибка регистрации запроса на оплату на стороне банка!");
    }

    private void updateRequest(PaymentRecord paymentRecord, String orderId) throws PaymentException {

        if ( paymentRecord.isPreProcessed() )
            throw new PaymentException("Запрос на оплату уже обработан!");

        paymentRecord.setOrderId( orderId );
        paymentRecord.setPaymentState( PaymentState.APPROVED );

        paymentRecordRepository.save( paymentRecord );

    }

    @Override
    public PaymentResponse updateRequestExtended(String orderId) throws PaymentException {

        PaymentRecord paymentRecord = paymentRecordRepository.findByOrderId( orderId );

        if ( paymentRecord == null )
            throw new PaymentException("Запрос на оплату не найден!");

        if ( paymentRecord.isProcessed() )
            throw new PaymentException("Запрос на оплату уже обработан!");

        return updateRequestExtended( paymentRecord );
    }

    @Override
    public PaymentResponse updateRequestExtended(PaymentRecord paymentRecord) throws PaymentException {
        ExtendedResponse extendedResponse = sendOrderStatusExtendedRequest( paymentRecord.getOrderId() );

        paymentRecord.updateRecord( extendedResponse );

        if ( extendedResponse.getActionCode().equals(0) )
            updateUserBalance( paymentRecord );

        paymentRecordRepository.save( paymentRecord );

        return new PaymentResponse( paymentRecord );
    }

    private ExtendedResponse sendOrderStatusExtendedRequest(String orderId) throws PaymentException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("userName", sbUsername);
        map.add("password", sbPassword);
        map.add("orderId", orderId);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<ExtendedResponse> response = restTemplate.exchange(sbOrderStatusExtendedUrl, HttpMethod.POST, entity, ExtendedResponse.class);

        if ( response.getStatusCodeValue() == 200 ) {
            ExtendedResponse extendedResponse = response.getBody();

            if ( extendedResponse != null ) {
                logger.info( "Got orderNumber: {}, orderStatus: {}", extendedResponse.getOrderNumber(), extendedResponse.getOrderStatus() );
                return extendedResponse;
            }
            else
                throw new PaymentException("Ошибка обработки запроса на оплату на стороне банка!");
        }
        else
            throw new PaymentException("Ошибка обработки запроса на оплату на стороне банка!");
    }

    private void updateUserBalance(PaymentRecord paymentRecord) throws PaymentException {
        User paymentUser = paymentRecord.getUser();

        if ( paymentUser == null )
            throw new PaymentException("Не найден пользователь, совершивший платеж!");

        if ( paymentRecord.getDepositedAmount() == null )
            throw new PaymentException("Банк не прислал оплаченную сумму!");

        Double oldBalance = paymentUser.getBalance();
        Double depositedAmount = paymentRecord.getDepositedAmount().doubleValue() / 100.0;

        paymentUser.setBalance( oldBalance + depositedAmount );

        userRepository.save( paymentUser );

        webSocketController.sendCounterRefreshMessage( paymentUser.getId() );

    }

    private String generateDepositOrderNumber() {
        return "deposit_test_" + ( paymentRecordRepository.count() + 1 );
    }

    private String generatePurchaseOrderNumber() {
        return "purchase_test_" + ( paymentRecordRepository.count() + 1 );
    }

}
