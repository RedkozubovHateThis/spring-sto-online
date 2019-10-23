package io.swagger.service.impl;

import io.swagger.controller.WebSocketController;
import io.swagger.firebird.model.DocumentOutHeader;
import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.model.ServiceGoodsAddon;
import io.swagger.firebird.model.ServiceWork;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.firebird.repository.ServiceGoodsAddonRepository;
import io.swagger.firebird.repository.ServiceWorkRepository;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.EventMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EventMessageServiceImpl implements EventMessageService {

    @Autowired
    private EventMessageRepository eventMessageRepository;
    @Autowired
    private WebSocketController webSocketController;
    @Autowired
    private DocumentServiceDetailRepository documentServiceDetailRepository;
    @Autowired
    private ServiceWorkRepository serviceWorkRepository;
    @Autowired
    private ServiceGoodsAddonRepository serviceGoodsAddonRepository;
    @Autowired
    private UserRepository userRepository;

    @Async
    @Override
    public void buildModeratorReplacementMessage(User sendUser, User targetUser) {

        EventMessage eventMessage = new EventMessage();
        eventMessage.setSendUser( sendUser );
        eventMessage.setTargetUser( targetUser );
        eventMessage.setMessageDate( new Date() );
        eventMessage.setMessageType( MessageType.MODERATOR_REPLACEMENT );

        eventMessageRepository.save(eventMessage);
        webSocketController.sendEventMessage(eventMessage, null);

    }

    @Async
    @Override
    public void buildServiceWorkChangeMessage(User sendUser, User targetUser, Integer documentId,
                                              Integer serviceWorkId, Boolean byPrice, Double price) {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne(documentId);
        if ( document == null ) return;

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) return;

        ServiceWork serviceWork = serviceWorkRepository.findOne(serviceWorkId);
        if ( serviceWork == null ) return;

        StringBuilder additionalInformation = new StringBuilder( serviceWork.getName() );
        additionalInformation.append(": ");

        if ( byPrice )
            additionalInformation.append("фиксированная стоимость - ");
        else
            additionalInformation.append("стоимость по Н/Ч - ");

        additionalInformation.append(price);

        buildEventMessage( sendUser, targetUser, documentId, documentOutHeader.getFullNumber(), additionalInformation.toString() );

    }

    @Override
    public void buildServiceGoodsAddonChangeMessage(User sendUser, User targetUser, Integer documentId,
                                                    Integer serviceGoodsAddonId, Double cost) {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne(documentId);
        if ( document == null ) return;

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) return;

        ServiceGoodsAddon serviceGoodsAddon = serviceGoodsAddonRepository.findOne(serviceGoodsAddonId);
        if ( serviceGoodsAddon == null ) return;

        StringBuilder additionalInformation = new StringBuilder( serviceGoodsAddon.getFullName() );
        additionalInformation
                .append(": ")
                .append("стоимость товара - ")
                .append(cost);

        buildEventMessage( sendUser, targetUser, documentId, documentOutHeader.getFullNumber(), additionalInformation.toString() );

    }

    private void buildEventMessage(User sendUser, User targetUser, Integer documentId, String documentNumber, String additionalInformation) {
        EventMessage eventMessage = new EventMessage();
        eventMessage.setSendUser( sendUser );
        eventMessage.setTargetUser( targetUser );
        eventMessage.setMessageDate( new Date() );
        eventMessage.setMessageType( MessageType.DOCUMENT_CHANGE );
        eventMessage.setDocumentId( documentId );
        eventMessage.setDocumentName( documentNumber );
        eventMessage.setAdditionalInformation( additionalInformation );

        eventMessageRepository.save(eventMessage);
        webSocketController.sendEventMessage(eventMessage, null);

        List<Long> adminIds = userRepository.collectUserIdsByRoleName("ADMIN");
        for (Long adminId : adminIds) {
            webSocketController.sendEventMessage(eventMessage, adminId);
        }
    }
}
