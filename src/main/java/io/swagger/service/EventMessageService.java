package io.swagger.service;

import io.swagger.postgres.model.security.User;
import org.springframework.scheduling.annotation.Async;

public interface EventMessageService {
    @Async
    void buildModeratorReplacementMessage(User sendUser, User targetUser);

    @Async
    void buildServiceWorkChangeMessage(User sendUser, User targetUser, Integer documentId, Integer serviceWorkId, Boolean byPrice, Double price);

    @Async
    void buildServiceGoodsAddonChangeMessage(User sendUser, User targetUser, Integer documentId, Integer serviceGoodsAddonId, Double cost);
}
