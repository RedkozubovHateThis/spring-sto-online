package io.swagger.response.api;

import io.swagger.postgres.model.security.User;
import lombok.Data;

@Data
public class EventMessageStatus {

    private boolean sendEventMessage;
    private User targetUser;

    public EventMessageStatus() {
        this.sendEventMessage = false;
    }

    public boolean getSending() {
        return sendEventMessage && targetUser != null;
    }

}
