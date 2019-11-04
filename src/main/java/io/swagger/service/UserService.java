package io.swagger.service;

import io.swagger.postgres.model.security.User;

public interface UserService {
    User setModerator(User user);

    void buildRegistrationEventMessage(User targetUser, User sendUser);

    String preparePhone(String phone);

    boolean isPhoneValid(String phone);

    void processPhone(User user);
}
