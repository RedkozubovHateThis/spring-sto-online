package io.swagger.service;

import io.swagger.postgres.model.Customer;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.response.exception.DataNotFoundException;

public interface UserService {

    String preparePhone(String phone);

    boolean isPhoneValid(String phone);

    boolean isEmailValid(String email);

    void processPhone(Profile profile);

    void processPhone(Customer customer);

    String processPhone(String phone);

    void generateUser(Profile profile, String roleName) throws Exception;

    void updateUser(Profile profile) throws Exception;

    Boolean isInnCorrect(String inn) throws DataNotFoundException;
}
