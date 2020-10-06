package io.swagger.service;

import io.swagger.postgres.model.security.User;
import io.swagger.response.integration.IntegrationBalanceRequest;
import io.swagger.response.integration.IntegrationBalanceResponse;
import io.swagger.response.integration.IntegrationUser;

public interface BalanceIntegrationService {
    IntegrationBalanceResponse processIntegrationBalance(IntegrationBalanceRequest integrationBalanceRequest, User user) throws IllegalArgumentException;
}
