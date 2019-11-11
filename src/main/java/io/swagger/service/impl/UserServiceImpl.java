package io.swagger.service.impl;

import io.swagger.controller.WebSocketController;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String PHONE_REGEXP = "^((\\+7|7|8)+([0-9]){10})$";
    private static final String EMAIL_REGEXP = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    @Autowired
    private EventMessageRepository eventMessageRepository;
    @Autowired
    private WebSocketController webSocketController;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User setModerator(User user) {

        User userModerator = null;

        if ( user.getModeratorId() != null ) {
            userModerator = userRepository.findOne( user.getModeratorId() );
        }

        if ( userModerator == null ) {

            List<User> moderators = userRepository.findUsersByRoleName("MODERATOR");

            for (User moderator : moderators) {

                if ( userModerator == null )
                    userModerator = moderator;
                else {

                    if ( moderator.getLastUserAcceptDate() == null ) {
                        userModerator = moderator;
                        break;
                    }
                    else if ( userModerator.getLastUserAcceptDate() == null ) {
                        break;
                    }
                    else if ( moderator.getLastUserAcceptDate().before( userModerator.getLastUserAcceptDate() ) ) {
                        userModerator = moderator;
                    }

                }

            }

        }

        if ( userModerator != null ) {
            logger.info( "Got moderator \"{}\" for user \"{}\"", userModerator.getFio(), user.getFio() );
            user.setModerator( userModerator );
            webSocketController.sendCounterRefreshMessage( userModerator.getId() );

            userModerator.setLastUserAcceptDate( new Date() );
            userRepository.save( userModerator );

            return userModerator;
        }
        else {
            logger.error("Moderator not found");
            return null;
        }

    }

    @Override
    public void buildRegistrationEventMessage(User targetUser, User sendUser) {

        EventMessage eventMessage = new EventMessage();
        eventMessage.setSendUser( sendUser );
        eventMessage.setTargetUser( targetUser );
        eventMessage.setMessageType( MessageType.USER_REGISTER );
        eventMessage.setMessageDate( new Date() );

        eventMessageRepository.save(eventMessage);

        webSocketController.sendEventMessage( eventMessage, targetUser.getId() );

    }

    @Override
    public String preparePhone(String phone) {
        String preparedPhone = phone.replaceAll("[^+\\d]", "");

        if ( !isPhoneValid( preparedPhone ) ) {
            logger.warn("Phone is not valid, trying to fix...");
            preparedPhone = fixPhone(preparedPhone);

            if ( !isPhoneValid( preparedPhone ) ) {
                logger.error("Phone is not valid after fixing...");
                return null;
            }
        }

        return preparedPhone;

    }

    private String fixPhone(String phone) {

        //Предполагаем, что забыли добавить 8 в начале телефона
        //Все коды регионов в России начинаются с 9
        if ( phone.length() == 10 && phone.charAt(0) == '9' ) {
            return '8' + phone;
        }
        else
            return phone;

    }

    @Override
    public boolean isPhoneValid(String phone) {
        return phone.matches(PHONE_REGEXP);
    }

    @Override
    public boolean isEmailValid(String email) {
        return email.matches(EMAIL_REGEXP);
    }

    @Override
    public void processPhone(User user) {
        String originalPhone = user.getPhone();

        if ( originalPhone.charAt(0) == '+' ) {
            user.setPhone( originalPhone.replaceAll("\\+7", "8") );
        }
        else if ( originalPhone.charAt(0) == '7' ) {
            user.setPhone( originalPhone.replaceFirst("7", "8") );
        }
    }

    @Override
    public String processPhone(String phone) {
        if ( phone.charAt(0) == '+' ) {
            return phone.replaceAll("\\+7", "8");
        }
        else if ( phone.charAt(0) == '7' ) {
            return phone.replaceFirst("7", "8");
        }

        return phone;
    }
}
