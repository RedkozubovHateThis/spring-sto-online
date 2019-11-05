package io.swagger.response;

import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.UploadFile;
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
    private String uploadFileName;
    private Boolean inVacation;
    private List<String> roles;

    public OpponentResponse(User opponent, ChatMessage lastMessage) {
        id = opponent.getId();
        fio = opponent.getFio();
        inVacation = opponent.getInVacation();

        Set<UserRole> roles = opponent.getRoles();
        this.roles = roles.stream().map( UserRole::getNameRus ).collect( Collectors.toList() );

        if ( lastMessage == null ) return;

        lastMessageText = lastMessage.getMessageText();
        lastMessageFromId = lastMessage.getFromUser().getId();

        UploadFile uploadFile = lastMessage.getUploadFile();
        if ( uploadFile != null )
            uploadFileName = uploadFile.getFileName();
    }

}
