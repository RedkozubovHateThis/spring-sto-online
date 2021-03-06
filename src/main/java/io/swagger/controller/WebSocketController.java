package io.swagger.controller;

import io.swagger.postgres.model.AdEntity;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.AdEntityResourceProcessor;
import io.swagger.response.EventMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WebSocketController {

    private final static Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdEntityResourceProcessor adEntityResourceProcessor;

    public void sendCounterRefreshMessage(User user) {

        try {
            template.convertAndSend( String.format( "/topic/counters/%s", user.getId() ), user.getId() );
        }
        catch ( IllegalArgumentException iae ) {
            logger.error( "Counter refresh message sending error: {}", iae.getMessage() );
        }

    }

    public void sendCounterRefreshMessageToAdmins() {

        try {
            List<Long> adminsIds = userRepository.collectUserIdsByRoleName("ADMIN");

            for (Long adminsId : adminsIds) {
                template.convertAndSend(
                        String.format( "/topic/counters/%s", adminsId ), adminsId
                );
            }
        }
        catch ( IllegalArgumentException iae ) {
            logger.error( "Counter refresh message sending error: {}", iae.getMessage() );
        }

    }

    public void sendEventMessage(EventMessage eventMessage, Long toId) {

        try {
            EventMessageResponse eventMessageResponse = new EventMessageResponse(eventMessage);
            template.convertAndSend( String.format( "/topic/event/%s",
                    toId != null ? toId :  eventMessageResponse.getToId() ),
                    eventMessageResponse );
        }
        catch ( IllegalArgumentException iae ) {
            logger.error( "Counter refresh message sending error: {}", iae.getMessage() );
        }

    }

    public void sendAdEntity(AdEntity adEntity) {

        try {
            if ( adEntity != null )
                template.convertAndSend(  "/topic/ad", adEntityResourceProcessor.toResource( adEntity, null ) );
            else
                template.convertAndSend("/topic/ad/clear", new Object());
        }
        catch ( Exception e ) {
            logger.error( "Ad sending error: {}", e.getMessage() );
        }

    }

}
