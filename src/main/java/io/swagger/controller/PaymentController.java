package io.swagger.controller;

import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.helper.DateHelper;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.PaymentRecordRepository;
import io.swagger.postgres.repository.SubscriptionRepository;
import io.swagger.postgres.repository.SubscriptionTypeRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.payment.PromisedAvailableResponse;
import io.swagger.response.payment.SubscriptionResponse;
import io.swagger.response.payment.SubscriptionTypeResponse;
import io.swagger.response.api.ApiResponse;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/secured/payment")
public class PaymentController {

    private final static Logger logger = LoggerFactory.getLogger( PaymentController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRecordRepository paymentRecordRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;
    @Autowired
    private DocumentServiceDetailRepository documentsRepository;

    @PutMapping("/registerRequest")
    public ResponseEntity registerRequest(@RequestParam("amount") Integer amount) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(403).body( new ApiResponse("Пополнение баланса доступно только для Автосервиса!") );

        try {
            return ResponseEntity.ok( paymentService.registerPayment(amount, currentUser) );
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка формирования запроса на оплату! Попробуйте повторить запрос позже.") );
        }
    }

    @PutMapping("/registerRequest/promised")
    public ResponseEntity registerPromisedRequest(@RequestParam("amount") Integer amount) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(403).body( new ApiResponse("Пополнение баланса доступно только для Автосервиса!") );

        try {

            PromisedAvailableResponse response = paymentService.isPromisedAvailable( currentUser );

            if ( !response.getIsAvailable() ) {
                String message = String.format("Оформление обещанного платежа будет доступно только после %s",
                        DateHelper.formatDate( response.getAvailableDate() ) );
                return ResponseEntity.status(400).body( new ApiResponse(message) );
            }

            paymentService.registerPromisedPayment(amount, currentUser);

            return ResponseEntity.ok().build();
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка формирования запроса на оплату! Попробуйте повторить запрос позже.") );
        }
    }

    @GetMapping("/registerRequest/promised/isAvailable")
    public ResponseEntity registerPromisedRequest() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(403).body( new ApiResponse("Запрос статуса обещанного платежа доступен только для Автосервиса!") );

        try {
            return ResponseEntity.ok( paymentService.isPromisedAvailable(currentUser) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка запроса статуса обещанного платежа! Попробуйте повторить запрос позже.") );
        }
    }

    @PutMapping("/updateRequest/extended")
    public ResponseEntity updateRequestExtended(@RequestParam("orderId") String orderId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(403).body( new ApiResponse("Пополнение баланса доступно только для Автосервиса!") );

        try {
            return ResponseEntity.ok( paymentService.updateRequestExtended(orderId) );
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка обработки запроса на оплату! Попробуйте повторить обработку запроса вручную.") );
        }

    }

    @GetMapping("/findAll")
    public ResponseEntity findAll(@RequestParam("fromDate") @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        List<PaymentRecord> paymentRecords = paymentRecordRepository.findAllByUserId( currentUser.getId(), fromDate, toDate );

        List<PaymentResponse> paymentResponses = paymentRecords.stream().map( paymentRecord -> {

            if ( paymentRecord.isNeedsProcessing() ) {
                try {
                    return paymentService.updateRequestExtended( paymentRecord );
                }
                catch ( PaymentException pe ) {
                    return new PaymentResponse( paymentRecord );
                }
            }
            else
                return new PaymentResponse( paymentRecord );

        } ).collect( Collectors.toList() );

        return ResponseEntity.ok( paymentResponses );

    }

    @GetMapping("/subscriptions/types/findAll")
    public ResponseEntity findAllSubscriptionTypes() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(404).build();

        List<SubscriptionTypeResponse> subscriptionTypeResponses = new ArrayList<>();
        List<SubscriptionType> subscriptionTypes = subscriptionTypeRepository.findAllAndOrderBySortPosition();

        for ( SubscriptionType type : subscriptionTypes ) {
            SubscriptionTypeResponse subscriptionTypeResponse = new SubscriptionTypeResponse( type );

            if ( type.getIsFree() ) {
                Boolean isAnyFormed = subscriptionRepository.isAnyIsFormed( currentUser.getId() );
                if ( isAnyFormed ) continue;
            }

            subscriptionTypeResponses.add( subscriptionTypeResponse );
        }

        return ResponseEntity.ok(subscriptionTypeResponses);

    }

    @GetMapping("/subscriptions/findAll")
    public ResponseEntity findAllSubscriptions() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();
        if ( UserHelper.isServiceLeader( currentUser ) && !currentUser.isServiceLeaderValid() )
            return ResponseEntity.status(404).build();
        else if ( UserHelper.isFreelancer( currentUser ) && !currentUser.isFreelancerValid() )
            return ResponseEntity.status(404).build();

        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId( currentUser.getId() );

        List<SubscriptionResponse> responses = subscriptions.stream().map( subscription -> {
            return new SubscriptionResponse(
                    subscription, countDocumentsRemains( currentUser, subscription )
            );
        } ).collect( Collectors.toList() );

        return ResponseEntity.ok(responses);

    }

    @GetMapping("/subscriptions/currentSubscription")
    public ResponseEntity findCurrentSubscription() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        Subscription subscription = currentUser.getCurrentSubscription();

        if ( subscription == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new SubscriptionResponse(
                subscription, countDocumentsRemains( currentUser, subscription)
        ) );

    }

    @PutMapping("/subscriptions/buy")
    public ResponseEntity buySubscription(@RequestParam("subscriptionTypeId") Long subscriptionTypeId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();
        if ( UserHelper.isServiceLeader( currentUser ) && !currentUser.isServiceLeaderValid() )
            return ResponseEntity.status(404).build();
        else if ( UserHelper.isFreelancer( currentUser ) && !currentUser.isFreelancerValid() )
            return ResponseEntity.status(404).build();

        try {
            Subscription subscription = paymentService.buySubscription( subscriptionTypeId, currentUser );

            return ResponseEntity.ok( new SubscriptionResponse(
                    subscription, countDocumentsRemains( currentUser, subscription)
            ) );
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка оформления тарифа! Попробуйте повторить покупку позже.") );
        }

    }

    @PutMapping("/subscriptions/gift")
    public ResponseEntity giftSubscription(@RequestParam("serviceLeaderId") Long serviceLeaderId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(404).body( new ApiResponse("У вас отсутсвуют необходимые права для выдачи тарифа!") );

        User serviceLeader = userRepository.findOne( serviceLeaderId );

        if ( serviceLeader == null || !UserHelper.isServiceLeaderOrFreelancer( serviceLeader ) )
            return ResponseEntity.status(404).body( new ApiResponse("Нельзя выдать тариф этому пользователю!") );

        SubscriptionType subscriptionType = subscriptionTypeRepository.findFreeSubscription();
        if ( subscriptionType == null )
            return ResponseEntity.status(404).body( new ApiResponse("Тариф не найден!") );

        try {
            Subscription subscription = paymentService.buySubscription( subscriptionType.getId(), serviceLeader );

            return ResponseEntity.ok( new SubscriptionResponse(
                    subscription, countDocumentsRemains( currentUser, subscription)
            ) );
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка оформления тарифа! Попробуйте повторить покупку позже.") );
        }

    }

    @PutMapping("/subscriptions/addon/buy")
    public ResponseEntity buySubscriptionAddon(@RequestParam("subscriptionId") Long subscriptionId,
                                               @RequestParam("documentsCount") Integer documentsCount) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        try {
            paymentService.buySubscriptionAddon( subscriptionId, documentsCount, currentUser );

            return ResponseEntity.ok().build();
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка дополнения тарифа! Попробуйте повторить покупку позже.") );
        }

    }

    @PutMapping("/subscriptions/updateRenewal")
    public ResponseEntity updateRenewalSubscription(@RequestParam("subscriptionTypeId") Long subscriptionTypeId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        try {
            paymentService.updateRenewalSubscription( subscriptionTypeId, currentUser );

            return ResponseEntity.ok().build();
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка установки тарифа по умолчанию! Попробуйте повторить позже.") );
        }

    }

    @PostMapping("/subscriptions/types/update")
    public ResponseEntity updateSubscriptionType(@RequestBody SubscriptionTypeResponse subscriptionTypeResponse) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        if ( subscriptionTypeResponse == null )
            return ResponseEntity.status(404).body( new ApiResponse( "Тариф не найден!" ) );

        SubscriptionType subscriptionType = subscriptionTypeRepository.findOne( subscriptionTypeResponse.getId() );
        if ( subscriptionType == null )
            return ResponseEntity.status(404).body( new ApiResponse( "Тариф не найден!" ) );

        if ( subscriptionTypeResponse.getDocumentsCount() == null || subscriptionTypeResponse.getDocumentsCount() <= 0 )
            return ResponseEntity.status(404).body( new ApiResponse( "Наверно указано количество документов!" ) );

        if ( subscriptionTypeResponse.getDurationDays() == null || subscriptionTypeResponse.getDurationDays() <= 0 )
            return ResponseEntity.status(404).body( new ApiResponse( "Наверно указано количество дней!" ) );

        if ( !subscriptionType.getIsFree() ) {

            if ( subscriptionTypeResponse.getCost() == null || subscriptionTypeResponse.getCost() <= 0 )
                return ResponseEntity.status(404).body( new ApiResponse( "Наверно указана стоимость тарифа!" ) );

            if ( subscriptionTypeResponse.getDocumentCost() == null || subscriptionTypeResponse.getDocumentCost() <= 0 )
                return ResponseEntity.status(404).body( new ApiResponse( "Наверно указана стоимость документов!" ) );

            subscriptionType.setCost( subscriptionTypeResponse.getCost() );
            subscriptionType.setDocumentCost( subscriptionTypeResponse.getDocumentCost() );
        }

        subscriptionType.setDocumentsCount( subscriptionTypeResponse.getDocumentsCount() );
        subscriptionType.setDurationDays( subscriptionTypeResponse.getDurationDays() );

        subscriptionTypeRepository.save( subscriptionType );

        return ResponseEntity.ok().build();
    }

    private Integer countDocumentsRemains(User user, Subscription subscription) {
        if ( !UserHelper.isServiceLeaderOrFreelancer( user ) || subscription == null ) return null;

        if ( UserHelper.isServiceLeader( user ) && !user.isServiceLeaderValid() ) return null;
        else if ( UserHelper.isFreelancer( user ) && !user.isFreelancerValid() ) return null;

        Integer count = null;

        if ( UserHelper.isServiceLeader( user ) )
            count = documentsRepository.countDocumentsByOrganizationIdAndDates(
                    user.getOrganizationId(),
                    subscription.getStartDate(),
                    subscription.getEndDate()
            );
        else if ( UserHelper.isFreelancer( user ) )
            count = documentsRepository.countDocumentsByOrganizationIdAndDatesAndManagerId(
                    user.getOrganizationId(),
                    subscription.getStartDate(),
                    subscription.getEndDate(),
                    user.getManagerId()
            );

        return Math.max( subscription.getDocumentsCount() - count, 0 );
    }
}
