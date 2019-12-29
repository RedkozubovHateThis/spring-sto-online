package io.swagger.controller;

import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.model.GoodsOutClient;
import io.swagger.firebird.model.ServiceGoodsAddon;
import io.swagger.firebird.model.ServiceWork;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.ChatMessageResponse;
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

    public void sendChatMessage(ChatMessage chatMessage) {

        try {
            ChatMessageResponse chatMessageResponse = new ChatMessageResponse(chatMessage);
            template.convertAndSend( String.format( "/topic/message/%s", chatMessageResponse.getToId() ), chatMessageResponse );
        }
        catch ( IllegalArgumentException iae ) {
            logger.error( "Chat message sending error: {}", iae.getMessage() );
        }

    }

    public void sendChatMessage(ChatMessage chatMessage, DocumentServiceDetail document, ServiceWork serviceWork,
                                ServiceGoodsAddon serviceGoodsAddon, GoodsOutClient goodsOutClient) {

        try {

            ChatMessageResponse chatMessageResponse;

            switch ( chatMessage.getChatMessageType() ) {
                case DOCUMENT: chatMessageResponse = new ChatMessageResponse( chatMessage, document ); break;
                case SERVICE_WORK: chatMessageResponse = new ChatMessageResponse( chatMessage, document, serviceWork); break;
                case SERVICE_GOODS_ADDON: chatMessageResponse = new ChatMessageResponse( chatMessage, document, serviceGoodsAddon); break;
                case CLIENT_GOODS_OUT: chatMessageResponse = new ChatMessageResponse( chatMessage, document, goodsOutClient); break;
                default: return;
            }

            template.convertAndSend( String.format( "/topic/message/%s", chatMessageResponse.getToId() ), chatMessageResponse );
        }
        catch ( IllegalArgumentException iae ) {
            logger.error( "Chat message sending error: {}", iae.getMessage() );
        }

    }

    public void sendCounterRefreshMessage(User user, Boolean toModerator, Boolean toAdmins) {

        try {
            template.convertAndSend( String.format( "/topic/counters/%s", user.getId() ), user.getId() );

            if ( toModerator && user.getModerator() != null )
                template.convertAndSend(
                        String.format( "/topic/counters/%s", user.getModerator().getId() ), user.getModerator().getId()
                );

            if ( toAdmins ) {

                List<Long> adminsIds = userRepository.collectUserIdsByRoleName("ADMIN");

                for (Long adminsId : adminsIds) {
                    template.convertAndSend(
                            String.format( "/topic/counters/%s", adminsId ), adminsId
                    );
                }

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

}
