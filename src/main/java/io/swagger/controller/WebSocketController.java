package io.swagger.controller;

import io.swagger.postgres.model.ChatMessage;
import io.swagger.response.ChatMessageResponse;
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

}
