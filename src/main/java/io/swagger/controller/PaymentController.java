package io.swagger.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.PaymentRecordRepository;
import io.swagger.postgres.repository.UserRepository;
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
}
