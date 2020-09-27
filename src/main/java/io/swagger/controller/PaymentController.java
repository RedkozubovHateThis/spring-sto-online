package io.swagger.controller;

import io.swagger.helper.DateHelper;
import io.swagger.helper.PaymentRecordSpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.enums.PaymentState;
import io.swagger.postgres.model.enums.PaymentType;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.PaymentRecordRepository;
import io.swagger.postgres.repository.SubscriptionRepository;
import io.swagger.postgres.repository.SubscriptionTypeRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.PaymentRecordResourceProcessor;
import io.swagger.postgres.resourceProcessor.SubscriptionResourceProcessor;
import io.swagger.postgres.resourceProcessor.SubscriptionTypeResourceProcessor;
import io.swagger.response.api.ApiResponse;
import io.swagger.response.exception.PaymentException;
import io.swagger.response.payment.PaymentResponse;
import io.swagger.response.payment.PromisedAvailableResponse;
import io.swagger.service.PaymentService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
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
    private SubscriptionResourceProcessor subscriptionResourceProcessor;
    @Autowired
    private SubscriptionTypeResourceProcessor subscriptionTypeResourceProcessor;
    @Autowired
    private PaymentRecordResourceProcessor paymentRecordResourceProcessor;

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

    @GetMapping("/paymentRecords")
    public ResponseEntity findAll(@RequestParam("fromDate") @DateTimeFormat(pattern = "dd.MM.yyyy") Date fromDate,
                                  @RequestParam("toDate") @DateTimeFormat(pattern = "dd.MM.yyyy") Date toDate) throws Exception {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        List<PaymentRecord> paymentRecords = paymentRecordRepository.findAllByUserId( currentUser.getId(), fromDate, toDate );
        return ResponseEntity.ok(
                paymentRecordResourceProcessor.toResourceList(
                        paymentRecords,
                        null,
                        (long) paymentRecords.size(),
                        null
                )
        );

    }

    @GetMapping("/paymentRecords/search/filter")
    public ResponseEntity filter(PaymentRecordsPayload payload, Pageable pageable) throws Exception {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        Specification<PaymentRecord> specification =
                PaymentRecordSpecificationBuilder.buildSpecification( payload );

        Page<PaymentRecord> paymentRecords = paymentRecordRepository.findAll(specification, pageable);
        return ResponseEntity.ok(
                paymentRecordResourceProcessor.toResourcePage(
                        paymentRecords,
                        null,
                        paymentRecordRepository.count( specification ),
                        pageable
                )
        );

    }

    @GetMapping("/subscriptionTypes")
    public ResponseEntity findAllSubscriptionTypes() throws Exception {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(404).build();

        List<SubscriptionType> subscriptionTypes = subscriptionTypeRepository.findAllAndOrderBySortPosition();

        return ResponseEntity.ok(
                subscriptionTypeResourceProcessor.toResourceList(
                        subscriptionTypes,
                        new ArrayList<>(),
                        ( (Integer) subscriptionTypes.size() ).longValue(),
                        null
                )
        );

    }

    @GetMapping("/subscriptions")
    public ResponseEntity findAllSubscriptions() throws Exception {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId( currentUser.getId() );

        return ResponseEntity.ok(
                subscriptionResourceProcessor.toResourceList(
                        subscriptions,
                        new ArrayList<>(),
                        ( (Integer) subscriptions.size() ).longValue(),
                        null
                )
        );

    }

    @PutMapping("/subscriptions/buy")
    public ResponseEntity buySubscription(@RequestParam("subscriptionTypeId") Long subscriptionTypeId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        try {
            Subscription subscription = paymentService.buySubscription( subscriptionTypeId, currentUser );

            return ResponseEntity.ok(
                    subscriptionResourceProcessor.toResource(subscription, null)
            );
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка оформления тарифа! Попробуйте повторить покупку позже.") );
        }

    }

    @PutMapping("/subscriptions/unsubscribe")
    public ResponseEntity updateRenewalSubscription(@RequestParam("subscriptionId") Long subscriptionId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        try {
            paymentService.unsubscribe( subscriptionId, currentUser );

            return ResponseEntity.ok().build();
        }
        catch ( PaymentException pe ) {
            return ResponseEntity.status(500).body( new ApiResponse( pe.getMessage() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return ResponseEntity.status(500).body( new ApiResponse("Ошибка отписки от тарифа! Попробуйте повторить позже!") );
        }

    }

    @Data
    public static class PaymentRecordsPayload {
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        private Date fromDate;
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        private Date toDate;
        private Long userId;
        private PaymentType paymentType;
        private PaymentState paymentState;
    }
}
