package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ChatMessageRepository;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.UploadFileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.ChatMessageResponse;
import io.swagger.response.EventMessageResponse;
import io.swagger.response.OpponentResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/secured/eventMessages")
@RestController
public class EventMessageController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventMessageRepository eventMessageRepository;

    @GetMapping("/findAll")
    public ResponseEntity findAll() {

        User currentUser = userRepository.findCurrentUser();
        List<EventMessage> eventMessages = null;

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
            eventMessages = eventMessageRepository.findAllByMessageType( MessageType.DOCUMENT_CHANGE );
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            eventMessages = eventMessageRepository.findAllByTargetUser( currentUser );
        }

        if ( eventMessages == null ) return ResponseEntity.status(404).build();

        List<EventMessageResponse> responses = eventMessages.stream().map(EventMessageResponse::new).collect( Collectors.toList() );

        return ResponseEntity.ok(responses);

    }

}
