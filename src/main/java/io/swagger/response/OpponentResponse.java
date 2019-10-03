package io.swagger.response;

import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.security.User;
import lombok.Data;

@Data
public class OpponentResponse {

    private Long id;
    private String fio;
    private String lastMessageText;
    private Long lastMessageFromId;
    private String uploadFileName;
    private Boolean inVacation;

    public OpponentResponse(User opponent, ChatMessage lastMessage) {
        id = opponent.getId();
        fio = opponent.getFio();
        inVacation = opponent.getInVacation();

        if ( lastMessage == null ) return;

        lastMessageText = lastMessage.getMessageText();
        lastMessageFromId = lastMessage.getFromUser().getId();

        UploadFile uploadFile = lastMessage.getUploadFile();
        if ( uploadFile != null )
            uploadFileName = uploadFile.getFileName();
    }

}
