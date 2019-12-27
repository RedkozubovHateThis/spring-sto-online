package io.swagger.controller;

import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.model.GoodsOutClient;
import io.swagger.firebird.model.ServiceGoodsAddon;
import io.swagger.firebird.model.ServiceWork;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.EventMessage;
import io.swagger.response.ChatMessageResponse;
import io.swagger.response.EventMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final static Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate template;

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

    //TODO: добавить авторассылку админам и модерам по параметрам
    public void sendCounterRefreshMessage(Long userId) {

        try {
            template.convertAndSend( String.format( "/topic/counters/%s", userId ), userId );
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
