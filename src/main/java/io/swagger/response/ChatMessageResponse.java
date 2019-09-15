package io.swagger.response;

import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.UploadFile;
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

    public ChatMessageResponse(ChatMessage chatMessage) throws IllegalArgumentException {

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

        UploadFile uploadFile = chatMessage.getUploadFile();
        if ( uploadFile != null ) {
            uploadFileUuid = uploadFile.getUuid();
            uploadFileName = uploadFile.getFileName();

            if ( uploadFile.getContentType().equals("image/png") ||
                    uploadFile.getContentType().equals("image/jpeg") )
                uploadFileIsImage = true;
        }

    }

}
