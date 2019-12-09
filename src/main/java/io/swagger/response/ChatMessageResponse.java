package io.swagger.response;

import io.swagger.firebird.model.*;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.enums.ChatMessageType;
import io.swagger.postgres.model.security.User;
import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageResponse {

    private String fromFio;
    private Long fromId;

    private String toFio;
    private Long toId;

    private Date messageDate;
    private String messageText;

    private String uploadFileUuid;
    private String uploadFileName;
    private Boolean uploadFileIsImage = false;

    private ChatMessageType chatMessageType;

    private Integer documentId;
    private String documentNumber;
    private String documentState;
    private Date documentDate;

    private Integer serviceWorkId;
    private String serviceWorkName;

    private Integer serviceGoodsAddonId;
    private String serviceGoodsAddonName;

    private Integer clientGoodsOutId;
    private String clientGoodsOutName;

    public ChatMessageResponse(ChatMessage chatMessage) throws IllegalArgumentException {

        setBaseInfo(chatMessage);

        UploadFile uploadFile = chatMessage.getUploadFile();
        if ( uploadFile != null ) {
            uploadFileUuid = uploadFile.getUuid();
            uploadFileName = uploadFile.getFileName();

            if ( uploadFile.getContentType().equals("image/png") ||
                    uploadFile.getContentType().equals("image/jpeg") )
                uploadFileIsImage = true;
        }

    }

    public ChatMessageResponse(ChatMessage chatMessage, DocumentServiceDetail document) throws IllegalArgumentException {

        setBaseInfo(chatMessage);
        setDocumentInfo(document);

    }

    public ChatMessageResponse(ChatMessage chatMessage, DocumentServiceDetail document, ServiceWork serviceWork)
            throws IllegalArgumentException {

        setBaseInfo(chatMessage);
        setDocumentInfo(document);

        if ( serviceWork == null ) return;

        serviceWorkId = serviceWork.getId();
        serviceWorkName = serviceWork.getName();

    }

    public ChatMessageResponse(ChatMessage chatMessage, DocumentServiceDetail document, ServiceGoodsAddon serviceGoodsAddon)
            throws IllegalArgumentException {

        setBaseInfo(chatMessage);
        setDocumentInfo(document);

        if ( serviceGoodsAddon == null ) return;

        serviceGoodsAddonId = serviceGoodsAddon.getId();
        serviceGoodsAddonName = serviceGoodsAddon.getFullName();

    }

    public ChatMessageResponse(ChatMessage chatMessage, DocumentServiceDetail document, GoodsOutClient goodsOutClient)
            throws IllegalArgumentException {

        setBaseInfo(chatMessage);
        setDocumentInfo(document);

        if ( goodsOutClient == null ) return;

        clientGoodsOutId = goodsOutClient.getId();
        clientGoodsOutName = goodsOutClient.getName();

    }

    private void setBaseInfo(ChatMessage chatMessage) throws IllegalArgumentException {

        if ( chatMessage == null ) throw new IllegalArgumentException("Chat message can not be null");

        User fromUser = chatMessage.getFromUser();
        User toUser = chatMessage.getToUser();

        if ( fromUser == null || toUser == null ) throw new IllegalArgumentException("Sending or receiving user can not be null");

        fromFio = fromUser.getFio();
        fromId = fromUser.getId();

        toFio = toUser.getFio();
        toId = toUser.getId();

        messageDate = chatMessage.getMessageDate();
        messageText = chatMessage.getMessageText();
        chatMessageType = chatMessage.getChatMessageType();
    }

    private void setDocumentInfo(DocumentServiceDetail document) {

        if ( document == null ) return;

        documentId = document.getId();
        documentDate = document.getDateStart();

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) return;

        documentState = documentOutHeader.getState() == 4 ? "Оформлен" : documentOutHeader.getState() == 2 ? "Черновик" : "Неизвестно";
        documentNumber = documentOutHeader.getFullNumber();

    }

}
