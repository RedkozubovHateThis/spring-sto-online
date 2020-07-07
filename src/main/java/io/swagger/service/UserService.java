package io.swagger.service;

import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;

public interface UserService {

    String preparePhone(String phone);

    boolean isPhoneValid(String phone);

    boolean isEmailValid(String email);

    void processPhone(Profile profile);

    String processPhone(String phone);

    void generateUser(Profile profile) throws Exception;
}
