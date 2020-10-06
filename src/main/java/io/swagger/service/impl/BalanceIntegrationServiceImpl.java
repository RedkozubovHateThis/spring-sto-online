package io.swagger.service.impl;

import io.swagger.helper.DateHelper;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.response.integration.IntegrationBalanceRequest;
import io.swagger.response.integration.IntegrationBalanceResponse;
import io.swagger.service.BalanceIntegrationService;
import io.swagger.service.PaymentService;
import io.swagger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceIntegrationServiceImpl implements BalanceIntegrationService {

    private final static Logger logger = LoggerFactory.getLogger(BalanceIntegrationService.class);

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;

    @Override
    public IntegrationBalanceResponse processIntegrationBalance(IntegrationBalanceRequest integrationBalanceRequest, User user) throws IllegalArgumentException {
        checkAndSetFields(integrationBalanceRequest);

        Profile profile = getProfile(integrationBalanceRequest);

        if ( profile.getUser() == null )
            throw new IllegalArgumentException("Профиль не найден");

        User serviceLeader = profile.getUser();

        if ( !UserHelper.isServiceLeaderOrFreelancer( serviceLeader ) )
            throw new IllegalArgumentException("Данный профиль не может вносить денежные средства и оформлять тарифы");

        if ( UserHelper.isServiceLeaderOrFreelancer( user ) && !user.getId().equals( serviceLeader.getId() ) )
            throw new IllegalArgumentException("Указаныый пользователь не может получать данные о балансе других профилей");

        IntegrationBalanceResponse integrationBalanceResponse = new IntegrationBalanceResponse();
        integrationBalanceResponse.setBalance( serviceLeader.getBalance() );

        Subscription adSubscription = serviceLeader.getCurrentAdSubscription();
        Subscription operatorSubscription = serviceLeader.getCurrentOperatorSubscription();

        if ( adSubscription != null ) {
            integrationBalanceResponse.setAdExpiresAt( DateHelper.formatDate( adSubscription.getEndDate() ) );
            if ( adSubscription.getType() != null && adSubscription.getIsRenewable() )
                integrationBalanceResponse.setAdIsRenewable( ( serviceLeader.getBalance() - adSubscription.getType().getCost() ) > 0 );
            else
                integrationBalanceResponse.setAdIsRenewable( false );
        }
        if ( operatorSubscription != null ) {

            if ( operatorSubscription.getType() != null ) {
                integrationBalanceResponse.setOperatorExpiresAt( paymentService.getRemainsDocuments( profile, operatorSubscription, operatorSubscription.getType() ) );
            }

            if ( operatorSubscription.getType() != null && operatorSubscription.getIsRenewable() ) {
                if ( adSubscription != null && adSubscription.getType() != null && adSubscription.getIsRenewable() )
                    integrationBalanceResponse.setOperatorIsRenewable( ( serviceLeader.getBalance() - adSubscription.getType().getCost() - operatorSubscription.getType().getCost() ) > 0 );
                else
                    integrationBalanceResponse.setOperatorIsRenewable( ( serviceLeader.getBalance() - operatorSubscription.getType().getCost() ) > 0 );
            }
            else
                integrationBalanceResponse.setOperatorIsRenewable( false );
        }

        return integrationBalanceResponse;
    }

    private Profile getProfile(IntegrationBalanceRequest integrationBalanceRequest) throws IllegalArgumentException {
        logger.info(" [ BALANCE INTEGRATION SERVICE ] Searching for profile... ");
        Profile profile = null;

        if ( !isFieldEmpty( integrationBalanceRequest.getIntegrationId() ) )
            profile = profileRepository.findOneByIntegrationId( integrationBalanceRequest.getIntegrationId() );
        if ( profile == null && !isFieldEmpty( integrationBalanceRequest.getPhone() ) )
            profile = profileRepository.findOneByPhone( integrationBalanceRequest.getPhone() );
        if ( profile == null && !isFieldEmpty( integrationBalanceRequest.getEmail() ) )
            profile = profileRepository.findOneByEmail( integrationBalanceRequest.getEmail() );

        if ( profile == null )
            throw new IllegalArgumentException("Профиль не найден");

        return profile;
    }

    private void checkAndSetFields(IntegrationBalanceRequest integrationBalanceRequest) throws IllegalArgumentException {
        if ( isFieldEmpty( integrationBalanceRequest.getIntegrationId() )
                && isFieldEmpty( integrationBalanceRequest.getPhone() )
                && isFieldEmpty( integrationBalanceRequest.getEmail() ) )
            throw new IllegalArgumentException("Не указано ни одно поле для поиска профиля");

        if ( !isFieldEmpty( integrationBalanceRequest.getPhone() ) ) {
            String oldPhone = integrationBalanceRequest.getPhone();
            integrationBalanceRequest.setPhone( userService.processPhone( integrationBalanceRequest.getPhone() ) );
            if ( !userService.isPhoneValid( integrationBalanceRequest.getPhone() ) )
                throw new IllegalArgumentException( String.format("Неверный формат телефона: %s", oldPhone) );
        }

        if ( !isFieldEmpty( integrationBalanceRequest.getEmail() ) && !userService.isEmailValid( integrationBalanceRequest.getEmail() ) )
            throw new IllegalArgumentException( String.format("Неверный формат email: %s", integrationBalanceRequest.getEmail()) );
    }

    private Boolean isFieldEmpty(String field) {
        return field == null || field.length() == 0;
    }
}
