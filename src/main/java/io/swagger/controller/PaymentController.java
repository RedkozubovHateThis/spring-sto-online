package io.swagger.controller;

import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.enums.SubscriptionType;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.PaymentRecordRepository;
import io.swagger.postgres.repository.SubscriptionRepository;
import io.swagger.postgres.repository.UserRepository;
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
    private DocumentServiceDetailRepository documentsRepository;

    @PutMapping("/registerRequest")
    public ResponseEntity registerRequest(@RequestParam("amount") Integer amount) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
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

    @PutMapping("/updateRequest/extended")
    public ResponseEntity updateRequestExtended(@RequestParam("orderId") String orderId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
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
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
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
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
            return ResponseEntity.status(404).build();

        List<SubscriptionTypeResponse> subscriptionTypeResponses = new ArrayList<>();

        for ( SubscriptionType type : SubscriptionType.values() ) {
            SubscriptionTypeResponse subscriptionTypeResponse = new SubscriptionTypeResponse( type );

            if ( type.getFree() ) {
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
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") ||
                currentUser.getOrganizationId() == null || !currentUser.getIsApproved() )
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
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
            return ResponseEntity.status(404).build();

        Subscription subscription = currentUser.getCurrentSubscription();

        if ( subscription == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new SubscriptionResponse(
                subscription, countDocumentsRemains( currentUser, subscription)
        ) );

    }

    @PutMapping("/subscriptions/buy")
    public ResponseEntity buySubscription(@RequestParam("subscriptionType") SubscriptionType subscriptionType) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
            return ResponseEntity.status(404).build();

        try {
            Subscription subscription = paymentService.buySubscription( subscriptionType, currentUser );

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
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
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
    public ResponseEntity updateRenewalSubscription(@RequestParam("subscriptionType") SubscriptionType subscriptionType) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") )
            return ResponseEntity.status(404).build();

        try {
            paymentService.updateRenewalSubscription( subscriptionType, currentUser );

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

    private Integer countDocumentsRemains(User user, Subscription subscription) {
        if ( !UserHelper.hasRole(user, "SERVICE_LEADER") || !user.getIsApproved() ||
                user.getOrganizationId() == null || subscription == null ) return null;

        Integer count = documentsRepository.countDocumentsByOrganizationIdAndDates(
                user.getOrganizationId(),
                subscription.getStartDate(),
                subscription.getEndDate()
        );

        return Math.max( subscription.getDocumentsCount() - count, 0 );
    }
}
