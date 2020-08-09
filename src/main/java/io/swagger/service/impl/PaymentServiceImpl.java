package io.swagger.service.impl;

import io.swagger.controller.WebSocketController;
import io.swagger.postgres.model.enums.PaymentState;
import io.swagger.postgres.model.enums.PaymentType;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionAddon;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.*;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.response.payment.PromisedAvailableResponse;
import io.swagger.response.payment.request.ExtendedResponse;
import io.swagger.response.payment.request.RegisterResponse;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;
    @Autowired
    private SubscriptionAddonRepository subscriptionAddonRepository;

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

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentState( PaymentState.CREATED );
        paymentRecord.setAmount( amount );
        paymentRecord.setCreateDate( new Date() );
        paymentRecord.setOrderNumber( generateDepositOrderNumber( user ) );
        paymentRecord.setUser( user );
        paymentRecord.setPaymentType( PaymentType.DEPOSIT );

        paymentRecordRepository.save( paymentRecord );

        RegisterResponse registerResponse = sendRegisterRequest( amount, paymentRecord.getOrderNumber() );

        updateRequest( paymentRecord, registerResponse.getOrderId() );

        return registerResponse;

    }

    @Override
    public void registerPromisedPayment(Integer amount, User user) throws PaymentException {

        Date now = new Date();

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentState( PaymentState.DEPOSITED );
        paymentRecord.setAmount( amount );
        paymentRecord.setCreateDate( now );
        paymentRecord.setRegisterDate( now );
        paymentRecord.setOrderNumber( generatePromisedOrderNumber( user ) );
        paymentRecord.setUser( user );
        paymentRecord.setPaymentType( PaymentType.PROMISED );
        paymentRecord.setIsExpired( false );
        paymentRecord.setExpirationDate( generateEndDate( now, 7, false ) );

        paymentRecordRepository.save( paymentRecord );

        updateUserBalance( paymentRecord );

    }

    @Override
    public PromisedAvailableResponse isPromisedAvailable(User user) {
        PaymentRecord promisedRecord = paymentRecordRepository.findLastPromisedRecordByUserId( user.getId() );
        PromisedAvailableResponse response = new PromisedAvailableResponse();

        if ( promisedRecord == null ) {
            response.setIsAvailable(true);

            List<SubscriptionType> subscriptionTypes = subscriptionTypeRepository.findNotFreeAndOrderBySortPosition();
            response.setAvailableCosts(
                    subscriptionTypes.stream().map(SubscriptionType::getCost).collect( Collectors.toList() )
            );

            return response;
        }
        else {

            Date now = new Date();
            Long difference = daysDifferent( promisedRecord.getCreateDate(), now );

            if ( difference <= 30 ) {
                response.setIsAvailable( false );
                response.setAvailableDate( generateEndDate( promisedRecord.getCreateDate(), 30, true ) );
                return response;
            }
            else {
                response.setIsAvailable( true );

                List<SubscriptionType> subscriptionTypes = subscriptionTypeRepository.findNotFreeAndOrderBySortPosition();
                response.setAvailableCosts(
                        subscriptionTypes.stream().map(SubscriptionType::getCost).collect( Collectors.toList() )
                );

                return response;
            }

        }
    }

    private Long daysDifferent(Date date1, Date date2) {
        long millis1 = date1.getTime();
        long millis2 = date2.getTime();

        long difference = millis2 - millis1;

        return TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
    }

    @Override
    public void processPromisedPayments() {

        List<PaymentRecord> expiringRecords = paymentRecordRepository.findAllExpiringPromisedRecords();

        for (PaymentRecord expiringRecord : expiringRecords) {

            logger.info( " [ PROMISED SCHEDULER ] Processing promised record \"{}\"...", expiringRecord.getOrderNumber() );

            expiringRecord.setIsExpired( true );
            paymentRecordRepository.save( expiringRecord );

            try {
                generatePaymentRecord( expiringRecord.getUser(), null, expiringRecord, new Date() );
            }
            catch ( PaymentException ignored ) {}
            catch ( Exception e ) {
                logger.error( "Got exception for promised record \"{}\" during updating user balance: {}", expiringRecord.getOrderNumber(), e.getMessage() );
                e.printStackTrace();
            }
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

    @Override
    public Subscription buySubscription(Long subscriptionTypeId, User user) throws PaymentException {

        SubscriptionType subscriptionType = subscriptionTypeRepository.findById( subscriptionTypeId ).orElse( null );
        if ( subscriptionType == null )
            throw new PaymentException("Тариф не найден!");

        if ( !subscriptionType.getIsFree() && user.getBalance() < subscriptionType.getCost() )
            throw new PaymentException("Недостаточно средств для оформления тарифа!");

        Date now = new Date();

        Subscription subscription = new Subscription( subscriptionType );
        boolean isSubscriptionChanged = false;

        Subscription currentSubscription = user.getCurrentSubscription();
        boolean isCurrentSubscriptionChanged = false;

        if ( currentSubscription != null ) {
            if ( currentSubscription.getEndDate().after( now ) ) {

//                int documentsCount = documentsRepository.countDocumentsByOrganizationIdAndDates(
//                        user.getOrganizationId(), currentSubscription.getStartDate(), currentSubscription.getEndDate()
//                );
                int documentsCount = 0;

                int availableDocuments = Math.max( currentSubscription.getDocumentsCount() - documentsCount, 0 );

                if ( availableDocuments > 0 )
                    throw new PaymentException("Текущий тариф еще не истек и имеет доступные заказ-наряды!");
                else {
                    currentSubscription.setEndDate(
                            generateEndDate( now, subscriptionType.getDurationDays(), false )
                    );
                    currentSubscription.updateDocumentsCount( subscriptionType );

                    currentSubscription.setIsClosedEarly(true);
                    isCurrentSubscriptionChanged = true;
                }
            }
            else if ( currentSubscription.getEndDate().before( now ) ) {
                subscription.setStartDate(
                        generateStartDate( now, false )
                );
                subscription.setEndDate(
                        generateEndDate( now, subscriptionType.getDurationDays(), false )
                );
                isSubscriptionChanged = true;
            }

        }
        else {
            subscription.setStartDate( generateStartDate( now, false ) );
            subscription.setEndDate( generateEndDate( now, subscriptionType.getDurationDays(), false ) );
            isSubscriptionChanged = true;
        }

        if ( isSubscriptionChanged ) {
            subscription.setUser( user );

            subscriptionRepository.save( subscription );
            user.setCurrentSubscription( subscription );

            if ( !subscription.getType().getIsFree() && user.getSubscriptionType() == null )
                user.setSubscriptionType( subscription.getType() );

            generatePaymentRecord( user, subscription, null, now );

            return subscription;
        }
        else if ( isCurrentSubscriptionChanged ) {
            subscriptionRepository.save( currentSubscription );
            generatePaymentRecord( user, currentSubscription, null, now );
            return currentSubscription;
        }

        throw new PaymentException("Ошибка покупки тарифа!");

    }

    @Override
    public void buySubscriptionAddon(Long subscriptionId, Integer documentsCount, User user) throws PaymentException {

        Subscription subscription = subscriptionRepository.findById( subscriptionId ).orElse( null );

        if ( subscription == null )
            throw new PaymentException("Выбранный тариф не найден!");

        Double cost = subscription.getDocumentCost() * documentsCount.doubleValue();

        if ( user.getBalance() < cost )
            throw new PaymentException("Недостаточно средств для дополнения тарифа!");

        Date now = new Date();

        SubscriptionAddon subscriptionAddon = new SubscriptionAddon();
        subscriptionAddon.setCost( cost );
        subscriptionAddon.setDate( now );
        subscriptionAddon.setDocumentsCount( documentsCount );
        subscriptionAddon.setSubscription( subscription );

        subscriptionAddonRepository.save( subscriptionAddon );

        subscription.applyAddon( subscriptionAddon );
        subscriptionRepository.save( subscription );

        generateAddonPaymentRecord( user, subscriptionAddon, now );

        webSocketController.sendCounterRefreshMessage( user );
        webSocketController.sendCounterRefreshMessageToAdmins();

    }

    @Override
    public void updateRenewalSubscription(Long subscriptionTypeId, User user) throws PaymentException {

        SubscriptionType subscriptionType = subscriptionTypeRepository.findById( subscriptionTypeId ).orElse( null );
        if ( subscriptionType == null )
            throw new PaymentException("Тариф не найден!");

//        if ( !subscriptionType.getFree() && user.getBalance() < subscriptionType.getCost() )
//            throw new PaymentException("Недостаточно средств для продления тарифа!");

        Subscription currentSubscription = user.getCurrentSubscription();

        Date now = new Date();

        if ( currentSubscription != null && currentSubscription.getEndDate().after( now ) ) {

            if ( user.getSubscriptionType() != null &&
                    user.getSubscriptionType().getId().equals( subscriptionType.getId() ) )
                user.setSubscriptionType( null );
            else
                user.setSubscriptionType( subscriptionType );

            userRepository.save( user );

            webSocketController.sendCounterRefreshMessage( user );
            webSocketController.sendCounterRefreshMessageToAdmins();
        }
        else
            throw new PaymentException("Текущий тариф не найден/истек!");
    }

    private void generatePaymentRecord(User user, Subscription subscription, PaymentRecord expiringRecord, Date now) throws PaymentException {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentState( PaymentState.DEPOSITED );
        paymentRecord.setCreateDate( now );
        paymentRecord.setRegisterDate( now );
        paymentRecord.setOrderNumber( generatePurchaseOrderNumber( user ) );
        paymentRecord.setUser( user );
        paymentRecord.setPaymentType( PaymentType.PURCHASE );

        if ( subscription != null ) {
            paymentRecord.setAmount( subscription.getType().getCost().intValue() * 100 );
            paymentRecord.setSubscription( subscription );
        }
        else if ( expiringRecord != null ) {
            paymentRecord.setAmount( expiringRecord.getAmount() );
            paymentRecord.setPromisedRecord( expiringRecord );
        }

        paymentRecordRepository.save( paymentRecord );

        updateUserBalance( paymentRecord );
    }

    private void generateAddonPaymentRecord(User user, SubscriptionAddon subscriptionAddon, Date now) throws PaymentException {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentState( PaymentState.DEPOSITED );
        paymentRecord.setAmount( subscriptionAddon.getCost().intValue() * 100 );
        paymentRecord.setCreateDate( now );
        paymentRecord.setRegisterDate( now );
        paymentRecord.setOrderNumber( generatePurchaseOrderNumber( user ) );
        paymentRecord.setUser( user );
        paymentRecord.setPaymentType( PaymentType.PURCHASE );
        paymentRecord.setSubscriptionAddon( subscriptionAddon );

        paymentRecordRepository.save( paymentRecord );

        updateUserBalance( paymentRecord );
    }

    private Date generateStartDate(Date startDate, Boolean toNextDay) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime( startDate );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );

        if ( toNextDay )
            calendar.add( Calendar.DAY_OF_MONTH, 1 );

        return calendar.getTime();
    }

    private Date generateEndDate(Date startDate, Integer durationDays, Boolean atDayStart) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime( startDate );

        calendar.add( Calendar.DAY_OF_MONTH, durationDays );

        if ( atDayStart ) {
            calendar.set( Calendar.HOUR_OF_DAY, 0 );
            calendar.set( Calendar.MINUTE, 0 );
            calendar.set( Calendar.SECOND, 0 );
            calendar.set( Calendar.MILLISECOND, 0 );
        }
        else {
            calendar.set( Calendar.HOUR_OF_DAY, 23 );
            calendar.set( Calendar.MINUTE, 59 );
            calendar.set( Calendar.SECOND, 59 );
            calendar.set( Calendar.MILLISECOND, 999 );
        }

        return calendar.getTime();
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

        if ( paymentRecord.getPaymentType().equals( PaymentType.DEPOSIT ) && paymentRecord.getDepositedAmount() == null )
            throw new PaymentException("Банк не прислал оплаченную сумму!");

        Double oldBalance = paymentUser.getBalance();

        switch ( paymentRecord.getPaymentType() ) {
            case PROMISED: {
                Double amount = paymentRecord.getAmount().doubleValue() / 100.0;
                paymentUser.setBalance( oldBalance + amount );
                break;
            }
            case DEPOSIT: {
                Double depositedAmount = paymentRecord.getDepositedAmount().doubleValue() / 100.0;
                paymentUser.setBalance( oldBalance + depositedAmount );
                break;
            }
            case PURCHASE: {
                Double amount = paymentRecord.getAmount().doubleValue() / 100.0;
                paymentUser.setBalance( oldBalance - amount );
            }
        }

        userRepository.save( paymentUser );

        webSocketController.sendCounterRefreshMessage( paymentUser );
        webSocketController.sendCounterRefreshMessageToAdmins();

    }

    private String generateDepositOrderNumber(User user) {
        return String.format("D: %s.%s",
                user.getId(),
                paymentRecordRepository.countByUserIdAndPaymentType( user.getId(), PaymentType.DEPOSIT.toString() ) + 1);
    }

    private String generatePromisedOrderNumber(User user) {
        return String.format("DP: %s.%s",
                user.getId(),
                paymentRecordRepository.countByUserIdAndPaymentType( user.getId(), PaymentType.PROMISED.toString() ) + 1);
    }

    private String generatePurchaseOrderNumber(User user) {
        return String.format("P: %s.%s",
                user.getId(),
                paymentRecordRepository.countByUserIdAndPaymentType( user.getId(), PaymentType.PURCHASE.toString() ) + 1);
    }

}
