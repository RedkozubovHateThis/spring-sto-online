package io.swagger.service;

import io.swagger.postgres.model.security.User;
import io.swagger.response.integration.IntegrationDocument;

import java.util.List;

public interface IntegrationService {
    void processIntegrationDocument(IntegrationDocument document, User user) throws IllegalArgumentException;
}
