package io.swagger.response;

import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import lombok.Data;

import java.util.Date;

@Data
public class EventMessageResponse {

    private String fromFio;
    private Long fromId;

    private String toFio;
    private Long toId;

    private MessageType messageType;
    private String additionalInformation;

    private Integer documentId;
    private String documentName;

    private Date messageDate;

    public EventMessageResponse(EventMessage eventMessage) throws IllegalArgumentException {

        if ( eventMessage == null ) throw new IllegalArgumentException("Event message can not be null");

        User sendUser = eventMessage.getSendUser();
        User targetUser = eventMessage.getTargetUser();

        if ( sendUser == null || targetUser == null ) throw new IllegalArgumentException("Send or target user can not be null");

        fromFio = sendUser.getFio();
        fromId = sendUser.getId();

        toFio = targetUser.getFio();
        toId = targetUser.getId();

        messageDate = eventMessage.getMessageDate();
        messageType = eventMessage.getMessageType();
        additionalInformation = eventMessage.getAdditionalInformation();

        documentId = eventMessage.getDocumentId();
        documentName = eventMessage.getDocumentName();

    }

}
