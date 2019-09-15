package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ChatMessageRepository;
import io.swagger.postgres.repository.UploadFileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.ChatMessageResponse;
import io.swagger.response.OpponentResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/secured/chat")
@RestController
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private WebSocketController webSocketController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UploadFileRepository uploadFileRepository;

    @PutMapping
    public ResponseEntity saveNewMessage(@RequestBody ChatMessagePayload payload) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( payload.getUploadFileId() == null &&
                ( payload.getMessageText() == null || payload.getMessageText().isEmpty() ) )
            return ResponseEntity.status(400).body("Сообщение не может быть пустым!");

        User toUser = userRepository.findOne( payload.getToUserId() );
        if ( toUser == null )
            return ResponseEntity.status(400).body("Получаемый пользователь не может быть пустым!");

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFromUser( currentUser );
        chatMessage.setToUser( toUser );
        chatMessage.setMessageDate( new Date() );
        chatMessage.setMessageText( payload.getMessageText() );

        Long uploadFileId = payload.getUploadFileId();
        UploadFile uploadFile = null;
        if ( uploadFileId != null ) {
            uploadFile = uploadFileRepository.findOne( uploadFileId );

            if ( uploadFile != null ) {
                chatMessage.setUploadFile( uploadFile );
            }
        }

        chatMessageRepository.save( chatMessage );

        webSocketController.sendChatMessage( chatMessage );

        if ( uploadFile != null ) {
            uploadFile.setChatMessage( chatMessage );
            uploadFileRepository.save( uploadFile );
        }

        return ResponseEntity.ok( new ChatMessageResponse(chatMessage) );

    }

    @GetMapping("/messages")
    public ResponseEntity findMessages(@RequestParam("toUserId") Long toUserId) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        List<ChatMessage> chatMessages = chatMessageRepository.findMessagesByUsers( currentUser.getId(), toUserId );

        if ( chatMessages.size() == 0 ) return ResponseEntity.status(404).build();

        List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
                .map(ChatMessageResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok( chatMessageResponses );

    }

    @GetMapping("/opponents")
    public ResponseEntity findOpponents() {

        User currentUser = userRepository.findCurrentUser();
        List<User> opponents = null;

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
            opponents = userRepository.findAllExceptSelf();
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            opponents = userRepository.findAllByModerator( currentUser.getId(), currentUser.getId() );
        }
        else if ( currentUser.getModeratorId() != null )
            opponents = userRepository.findAllModerators( currentUser.getModeratorId(), currentUser.getId() );

        if ( opponents == null ) return ResponseEntity.status(404).build();

        List<OpponentResponse> responses = opponents.stream().map( opponent -> {

            ChatMessage lastMessage = chatMessageRepository.findLastMessageByUsers( currentUser.getId(), opponent.getId() );
            return new OpponentResponse( opponent, lastMessage );

        } ).collect( Collectors.toList() );

        return ResponseEntity.ok(responses);

    }

    @Data
    private static class ChatMessagePayload {
        private String messageText;
        private Long toUserId;
        private Long uploadFileId;
    }

}
