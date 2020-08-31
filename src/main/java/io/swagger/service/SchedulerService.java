package io.swagger.service;

import io.swagger.controller.WebSocketController;
import io.swagger.postgres.model.AdEntity;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.*;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AdEntityRepository adEntityRepository;
    @Autowired
    private WebSocketController webSocketController;

    @Value("${scheduler.disabled}")
    private Boolean isSchedulerDisabled;

    private volatile AdEntity currentAdEntity = null;

    @Scheduled(fixedRate = 600000)
    public void scheduleFixedRateTask() {
        if ( isSchedulerDisabled ) return;

        logger.info(" [ SCHEDULER SERVICE ] Starting ad rotation...");
        updateAd();
    }

    @Scheduled(cron = "0 15 0 * * *")
    public void scheduleSubscriptions() {

        if ( isSchedulerDisabled ) return;

        List<User> serviceLeaders = userRepository.findUsersByRoleNames(Arrays.asList("SERVICE_LEADER", "FREELANCER"));

        Date now = new Date();

        for (User serviceLeader : serviceLeaders) {

            Subscription currentAdSubscription = serviceLeader.getCurrentAdSubscription();
            Subscription currentOperatorSubscription = serviceLeader.getCurrentAdSubscription();

            processSubscription(currentAdSubscription, serviceLeader, now);
            processSubscription(currentOperatorSubscription, serviceLeader, now);

        }

        paymentService.processPromisedPayments();
    }

    private void processSubscription(Subscription currentSubscription, User serviceLeader, Date now) {
        if ( currentSubscription == null || currentSubscription.getEndDate().after( now ) ) return;

        logger.info(" [ SUBSCRIPTION SCHEDULER ] ------------------ ");
        logger.info(" [ SUBSCRIPTION SCHEDULER ] Processing user \"{}\" and subscription type \"{}\"...", serviceLeader.getFio(), currentSubscription.getName() );

        if ( !currentSubscription.getIsRenewable() ) {
            logger.warn(" [ SUBSCRIPTION SCHEDULER ] Can not renew not renewable subscription...");
            return;
        }

        Long renewalTypeId;

        renewalTypeId = currentSubscription.getType().getId();

        try {
            Subscription subscription = paymentService.buySubscription( renewalTypeId, serviceLeader );

            logger.info( " [ SUBSCRIPTION SCHEDULER ] Successfully bought new subscription \"{}\" for user \"{}\"...",
                    subscription.getName(), serviceLeader.getFio() );
        }
        catch ( PaymentException ignored ) {}
        catch ( Exception e ) {
            logger.error( "Got exception for user \"{}\" [ {} ] during buying: {}", serviceLeader.getFio(), serviceLeader.getId(), e.getMessage() );
            e.printStackTrace();
        }
    }

    private void updateAd() {
        try {
            webSocketController.sendAdEntity( getAdEntity() );
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }
    }

    private AdEntity getAdEntity() throws DataNotFoundException {
        AdEntity adEntity;

        if ( currentAdEntity != null ) {
            adEntity = adEntityRepository.findNextAdEntity( currentAdEntity.getCreateDate() );
        }
        else {
            adEntity = adEntityRepository.findCurrentAdEntity();
        }

        if ( adEntity == null )
            adEntity = adEntityRepository.findFirstAdEntity();

        if ( adEntity == null )
            throw new DataNotFoundException();

        currentAdEntity = adEntity;

        return adEntity;
    }

    public AdEntity getCurrentAdEntity() throws DataNotFoundException {
        if ( currentAdEntity == null )
            return getAdEntity();

        return currentAdEntity;
    }

}
