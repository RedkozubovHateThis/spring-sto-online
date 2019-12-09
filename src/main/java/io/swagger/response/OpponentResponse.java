package io.swagger.response;

import io.swagger.firebird.model.*;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.enums.ChatMessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class OpponentResponse {

    private Long id;
    private String fio;

    private String lastMessageText;
    private Long lastMessageFromId;
    private ChatMessageType lastMessageType;

    private String lastMessageDocumentNumber;
    private String lastMessageServiceWorkName;
    private String lastMessageServiceGoodsAddonName;
    private String lastMessageClientGoodsOutName;

    private String uploadFileName;
    private Boolean inVacation;
    private List<String> roles;

    public OpponentResponse(User opponent) {
        setOpponentInfo(opponent);
    }

    public OpponentResponse(User opponent, ChatMessage lastMessage) {
        setOpponentInfo(opponent);

        if ( lastMessage == null ) return;
        setBaseMessageInfo(lastMessage);

        lastMessageText = lastMessage.getMessageText();

        UploadFile uploadFile = lastMessage.getUploadFile();
        if ( uploadFile != null )
            uploadFileName = uploadFile.getFileName();
    }

    public OpponentResponse(User opponent, ChatMessage lastMessage, DocumentServiceDetail document) {
        setOpponentInfo(opponent);

        if ( lastMessage == null ) return;
        setBaseMessageInfo(lastMessage);
        setBaseDocumentInfo(document);
    }

    public OpponentResponse(User opponent, ChatMessage lastMessage, DocumentServiceDetail document, ServiceWork serviceWork) {
        setOpponentInfo(opponent);

        if ( lastMessage == null ) return;
        setBaseMessageInfo(lastMessage);
        setBaseDocumentInfo(document);

        if ( serviceWork == null ) return;
        lastMessageServiceWorkName = serviceWork.getName();
    }

    public OpponentResponse(User opponent, ChatMessage lastMessage, DocumentServiceDetail document, ServiceGoodsAddon serviceGoodsAddon) {
        setOpponentInfo(opponent);

        if ( lastMessage == null ) return;
        setBaseMessageInfo(lastMessage);
        setBaseDocumentInfo(document);

        if ( serviceGoodsAddon == null ) return;
        lastMessageServiceGoodsAddonName = serviceGoodsAddon.getFullName();
    }

    public OpponentResponse(User opponent, ChatMessage lastMessage, DocumentServiceDetail document, GoodsOutClient goodsOutClient) {
        setOpponentInfo(opponent);

        if ( lastMessage == null ) return;
        setBaseMessageInfo(lastMessage);
        setBaseDocumentInfo(document);

        if ( goodsOutClient == null ) return;
        lastMessageClientGoodsOutName = goodsOutClient.getName();
    }

    private void setOpponentInfo(User opponent) {
        id = opponent.getId();
        fio = opponent.getFio();
        inVacation = opponent.getInVacation();

        Set<UserRole> roles = opponent.getRoles();
        this.roles = roles.stream().map( UserRole::getNameRus ).collect( Collectors.toList() );
    }

    private void setBaseMessageInfo( ChatMessage lastMessage) {
        lastMessageFromId = lastMessage.getFromUser().getId();
        lastMessageType = lastMessage.getChatMessageType();
    }

    private void setBaseDocumentInfo(DocumentServiceDetail document) {
        if ( document == null ) return;

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) return;

        lastMessageDocumentNumber = documentOutHeader.getFullNumber();
    }

}
