package io.swagger.controller;

import io.swagger.firebird.model.*;
import io.swagger.firebird.repository.*;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.enums.ChatMessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ChatMessageRepository;
import io.swagger.postgres.repository.UploadFileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.ChatMessageResponse;
import io.swagger.response.OpponentResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @Autowired
    private DocumentServiceDetailRepository documentServiceDetailRepository;
    @Autowired
    private ServiceWorkRepository serviceWorkRepository;
    @Autowired
    private ServiceGoodsAddonRepository serviceGoodsAddonRepository;
    @Autowired
    private GoodsOutClientRepository goodsOutClientRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @PutMapping
    public ResponseEntity saveNewMessage(@RequestBody ChatMessagePayload payload) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( UserHelper.isClient( currentUser ) )
            return ResponseEntity.status(400).body("У вас нет прав отправлять сообщения!");

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getIsAccessRestricted() )
            return ResponseEntity.status(400).body("У вас нет прав отправлять сообщения!");

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
        chatMessage.setChatMessageType( ChatMessageType.TEXT );

        Long uploadFileId = payload.getUploadFileId();
        UploadFile uploadFile = null;
        if ( uploadFileId != null ) {
            uploadFile = uploadFileRepository.findOne( uploadFileId );

            if ( uploadFile != null ) {
                chatMessage.setUploadFile( uploadFile );
                chatMessage.setChatMessageType( ChatMessageType.FILE );
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

    @PutMapping("/share")
    public ResponseEntity saveShareMessage(@RequestBody ChatSharePayload payload) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( UserHelper.isClient( currentUser ) )
            return ResponseEntity.status(400).body("У вас нет прав отправлять сообщения!");

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getIsAccessRestricted() )
            return ResponseEntity.status(400).body("У вас нет прав отправлять сообщения!");

        if ( payload.getDocumentId() == null )
            return ResponseEntity.status(400).body("Сообщение не может быть пустым!");

        User toUser = userRepository.findOne( payload.getToUserId() );
        if ( toUser == null )
            return ResponseEntity.status(400).body("Получаемый пользователь не может быть пустым!");

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFromUser( currentUser );
        chatMessage.setToUser( toUser );
        chatMessage.setMessageDate( new Date() );
        chatMessage.setMessageText( payload.getMessageText() );
        chatMessage.setDocumentId( payload.getDocumentId() );
        chatMessage.setChatMessageType( ChatMessageType.DOCUMENT );

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( payload.getDocumentId() );
        ServiceWork serviceWork = null;
        ServiceGoodsAddon serviceGoodsAddon = null;
        GoodsOutClient goodsOutClient = null;

        if ( payload.getServiceWorkId() != null ) {
            serviceWork = serviceWorkRepository.findOne( payload.getServiceWorkId() );

            if ( serviceWork != null ) {
                chatMessage.setServiceWorkId( serviceWork.getId() );
                chatMessage.setChatMessageType( ChatMessageType.SERVICE_WORK );
            }
        }
        else if ( payload.getServiceGoodsAddonId() != null ) {
            serviceGoodsAddon = serviceGoodsAddonRepository.findOne( payload.getServiceGoodsAddonId() );

            if ( serviceGoodsAddon != null ) {
                chatMessage.setServiceGoodsAddonId( serviceGoodsAddon.getId() );
                chatMessage.setChatMessageType( ChatMessageType.SERVICE_GOODS_ADDON );
            }
        }
        else if ( payload.getClientGoodsOutId() != null ) {
            goodsOutClient = goodsOutClientRepository.findOne( payload.getClientGoodsOutId() );

            if ( goodsOutClient != null ) {
                chatMessage.setClientGoodsOutId( payload.getClientGoodsOutId() );
                chatMessage.setChatMessageType( ChatMessageType.CLIENT_GOODS_OUT );
            }
        }

        chatMessageRepository.save( chatMessage );

        webSocketController.sendChatMessage( chatMessage, document, serviceWork, serviceGoodsAddon, goodsOutClient );

        return ResponseEntity.ok().build();

    }

    @GetMapping("/messages")
    public ResponseEntity findMessages(@RequestParam("toUserId") Long toUserId) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( UserHelper.isClient( currentUser ) )
            return ResponseEntity.status(404).build();

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getIsAccessRestricted() )
            return ResponseEntity.status(404).build();

        List<ChatMessage> chatMessages = chatMessageRepository.findMessagesByUsers( currentUser.getId(), toUserId );

        if ( chatMessages.size() == 0 ) return ResponseEntity.status(404).build();

        List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
                .map( chatMessage -> {

                    switch ( chatMessage.getChatMessageType() ) {
                        case DOCUMENT: return new ChatMessageResponse(
                                chatMessage,
                                documentServiceDetailRepository.findOne( chatMessage.getDocumentId() )
                        );
                        case SERVICE_WORK: return new ChatMessageResponse(
                                chatMessage,
                                documentServiceDetailRepository.findOne( chatMessage.getDocumentId() ),
                                serviceWorkRepository.findOne( chatMessage.getServiceWorkId() )
                        );
                        case SERVICE_GOODS_ADDON: return new ChatMessageResponse(
                                chatMessage,
                                documentServiceDetailRepository.findOne( chatMessage.getDocumentId() ),
                                serviceGoodsAddonRepository.findOne( chatMessage.getServiceGoodsAddonId() )
                        );
                        case CLIENT_GOODS_OUT: return new ChatMessageResponse(
                                chatMessage,
                                documentServiceDetailRepository.findOne( chatMessage.getDocumentId() ),
                                goodsOutClientRepository.findOne( chatMessage.getClientGoodsOutId() )
                        );
                        default: return new ChatMessageResponse( chatMessage );
                    }

                } ).collect(Collectors.toList());

        return ResponseEntity.ok( chatMessageResponses );

    }

    @GetMapping("/greet")
    public ResponseEntity createGreetMessage() {

        if ( !demoDomain ) return ResponseEntity.status(401).build();

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser.getModeratorId() == null ) return ResponseEntity.status(400).build();

        User fromUser = userRepository.findOne( currentUser.getModeratorId() );
        if ( fromUser == null ) return ResponseEntity.status(400).build();

        String messageText = "В чате можно уточнить информацию у вашего менеджера.";

//        String messageText = "Приветствуем вас в демонстрационной версии приложения BUROMOTORS. " +
//                "На странице \"Главная\" вы можете увидеть 5 последних заказ-нарядов. " +
//                "В чате вы можете задать все интересующие вас вопросы вашему модератору. " +
//                "На странице \"Заказ-наряды\" вы можете увидеть все ваши заказ-наряды с возможностью фильтрации. " +
//                "Так-же, в самом заказ-наряде, вы можете редактировать стоимость работ и запчастей. " +
//                "На странице \"Профиль\" вы можете увидеть и изменить вашу персональную информацию. " +
//                "На странице \"Отчеты\" вы можете увидеть и выгрузить отчеты \"Выручка по слесарям\" и " +
//                "\"Отчет о реализации\" за выбранный вами период.";

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFromUser( fromUser );
        chatMessage.setToUser( currentUser );
        chatMessage.setMessageDate( new Date() );
        chatMessage.setMessageText( messageText );
        chatMessage.setChatMessageType( ChatMessageType.TEXT );

        chatMessageRepository.save( chatMessage );

        webSocketController.sendChatMessage( chatMessage );

        return ResponseEntity.ok( new ChatMessageResponse(chatMessage) );

    }

    @GetMapping("/opponents")
    public ResponseEntity findOpponents() {

        User currentUser = userRepository.findCurrentUser();
        List<User> opponents = null;

        if ( UserHelper.isAdmin( currentUser ) ) {
            opponents = userRepository.findAllOpponentsByAdmin( currentUser.getUsername() );
        }
        else if ( UserHelper.isModerator( currentUser ) ) {
            opponents = userRepository.findAllOpponentsByModerator( currentUser.getId(), currentUser.getId() );
        }
        else if ( UserHelper.isServiceLeaderOrFreelancer( currentUser )
                && currentUser.getModeratorId() != null && !currentUser.getIsAccessRestricted() )
            opponents = userRepository.findAllOpponentsModerators( currentUser.getModeratorId(), currentUser.getId() );

        if ( opponents == null ) return ResponseEntity.status(404).build();

        List<OpponentResponse> responses = opponents.stream().map( opponent -> {

            ChatMessage lastMessage = chatMessageRepository.findLastMessageByUsers( currentUser.getId(), opponent.getId() );

            if ( lastMessage != null ) {
                switch ( lastMessage.getChatMessageType() ) {
                    case DOCUMENT: return new OpponentResponse(
                            opponent, lastMessage,
                            documentServiceDetailRepository.findOne( lastMessage.getDocumentId() )
                    );
                    case SERVICE_WORK: return new OpponentResponse(
                            opponent, lastMessage,
                            documentServiceDetailRepository.findOne( lastMessage.getDocumentId() ),
                            serviceWorkRepository.findOne( lastMessage.getServiceWorkId() )
                    );
                    case SERVICE_GOODS_ADDON: return new OpponentResponse(
                            opponent, lastMessage,
                            documentServiceDetailRepository.findOne( lastMessage.getDocumentId() ),
                            serviceGoodsAddonRepository.findOne( lastMessage.getServiceGoodsAddonId() )
                    );
                    case CLIENT_GOODS_OUT: return new OpponentResponse(
                            opponent, lastMessage,
                            documentServiceDetailRepository.findOne( lastMessage.getDocumentId() ),
                            goodsOutClientRepository.findOne( lastMessage.getClientGoodsOutId() )
                    );
                    default: return new OpponentResponse( opponent, lastMessage );
                }
            }

            return new OpponentResponse( opponent );

        } ).collect( Collectors.toList() );

        return ResponseEntity.ok(responses);

    }

    @GetMapping("/share/opponents")
    public ResponseEntity findShareOpponents(@RequestParam("documentId") Integer documentId) {
        User currentUser = userRepository.findCurrentUser();

        List<User> opponents = new ArrayList<>();

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) ) {
            findModerator(currentUser, opponents);
        }
        else if ( UserHelper.isModerator( currentUser ) ) {
            User serviceLeader = findServiceLeader( documentId );

            if ( serviceLeader != null )
                opponents.add( serviceLeader );
        }
        else if ( UserHelper.isAdmin( currentUser ) ) {
            User serviceLeader = findServiceLeader( documentId );

            if ( serviceLeader != null ) {
                opponents.add( serviceLeader );
                findModerator( serviceLeader, opponents );
            }
        }
        else
            return ResponseEntity.status(404).build();

        List<OpponentResponse> responses = opponents.stream().map(OpponentResponse::new).collect( Collectors.toList() );

        return ResponseEntity.ok(responses);
    }

    private void findModerator(User user, List<User> opponents) {

        if ( user == null ) return;

        User moderator = user.getModerator();
        if ( moderator != null ) {

            if ( !moderator.getInVacation() )
                opponents.add( moderator );
            else {
                User replacementModerator = moderator.getReplacementModerator();

                if ( replacementModerator != null && !replacementModerator.getInVacation() )
                    opponents.add( replacementModerator );
            }

        }
    }

    private User findServiceLeader(Integer documentId) {
        Organization organization = organizationRepository.findOrganizationByDocumentId(documentId);
        if ( organization == null ) return null;

        return userRepository.findUserByOrganizationId( organization.getId() );
    }

    @Data
    private static class ChatMessagePayload {
        private String messageText;
        private Long toUserId;
        private Long uploadFileId;
    }

    @Data
    private static class ChatSharePayload {
        private Long toUserId;
        private String messageText;
        private Integer documentId;
        private Integer serviceWorkId;
        private Integer serviceGoodsAddonId;
        private Integer clientGoodsOutId;
    }

}
