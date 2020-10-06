package io.swagger.service;

import io.swagger.postgres.model.security.User;
import io.swagger.response.integration.IntegrationUser;

public interface UserIntegrationService {
    void processIntegrationUser(IntegrationUser integrationUser, User user) throws IllegalArgumentException;
}
