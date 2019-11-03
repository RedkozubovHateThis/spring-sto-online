package io.swagger.controller;

import io.swagger.firebird.repository.DocumentOutHeaderRepository;
import io.swagger.firebird.repository.ServiceGoodsAddonRepository;
import io.swagger.firebird.repository.ServiceWorkRepository;
import io.swagger.helper.DocumentSpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.firebird.DocumentResponse;
import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.service.EventMessageService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/secured/documents")
@RestController
public class DocumentDetailServiceController {

    @Autowired
    private DocumentServiceDetailRepository documentsRepository;

    @Autowired
    private DocumentOutHeaderRepository documentOutHeaderRepository;

    @Autowired
    private ServiceWorkRepository serviceWorkRepository;

    @Autowired
    private ServiceGoodsAddonRepository serviceGoodsAddonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private EventMessageService eventMessageService;

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable, FilterPayload filterPayload) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        webSocketController.sendCounterRefreshMessage( currentUser.getId() );

        if ( UserHelper.hasRole(currentUser, "CLIENT") &&
                ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) )
            return ResponseEntity.status(404).build();

        else if ( UserHelper.hasRole(currentUser, "SERVICE_LEADER") &&
                ( currentUser.getOrganizationId() == null || !currentUser.getIsApproved() ) )
            return ResponseEntity.status(404).build();

        Specification<DocumentServiceDetail> specification;

        if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            List<Integer> organizationIds = userRepository.collectOrganizationIds( currentUser.getId() );

            if ( clientIds.size() == 0 && organizationIds.size() == 0 )
                return ResponseEntity.status(404).build();

            specification = DocumentSpecificationBuilder.buildSpecificationList(clientIds, organizationIds, currentUser, filterPayload);
        }
        else
            specification = DocumentSpecificationBuilder.buildSpecificationList(currentUser, filterPayload);

        Page<DocumentServiceDetail> result = documentsRepository.findAll(specification, pageable);

        List<DocumentServiceDetail> resultList = result.getContent();
        List<DocumentResponse> responseList = resultList.stream()
                .map(DocumentResponse::new).collect( Collectors.toList() );

        Page<DocumentResponse> responsePage = new PageImpl<>(responseList, pageable, result.getTotalElements());

        return ResponseEntity.ok( responsePage );

    }

    @GetMapping("/eventMessages/findAll")
    public ResponseEntity findEventMessagesDocuments() {

        User currentUser = userRepository.findCurrentUser();
        List<Integer> documentIds = null;

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
            documentIds = userRepository.collectDocumentIdsByAdmin();
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            documentIds = userRepository.collectDocumentIds( currentUser.getId() );
        }
        else
            return ResponseEntity.status(404).build();

        if ( documentIds.size() == 0 ) return ResponseEntity.status(404).build();

        List<DocumentServiceDetail> documents = documentsRepository.findByIds(documentIds);

        return ResponseEntity.ok( documents.stream()
                .map(DocumentResponse::new).collect( Collectors.toList() ) );

    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Integer id) {

        User currentUser = userRepository.findCurrentUser();
        DocumentServiceDetail result;

        if ( UserHelper.hasRole( currentUser, "CLIENT" ) ) {
            if ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) return ResponseEntity.status(403).build();
            result = documentsRepository.findOneByClientId( id, currentUser.getClientId() );
        }
        else if ( UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) ) {
            if ( currentUser.getOrganizationId() == null || !currentUser.getIsApproved() ) return ResponseEntity.status(403).build();
            result = documentsRepository.findOneByOrganizationId( id, currentUser.getOrganizationId() );
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            List<Integer> organizationIds = userRepository.collectOrganizationIds( currentUser.getId() );

            if ( clientIds.size() == 0 && organizationIds.size() == 0 )
                return ResponseEntity.status(403).build();
            else if ( clientIds.size() > 0 && organizationIds.size() == 0 )
                result = documentsRepository.findOneByClientIds( id, clientIds );
            else if ( clientIds.size() == 0 )
                result = documentsRepository.findOneByOrganizationIds( id, organizationIds );
            else
                result = documentsRepository.findOneByClientIdsAndOrganizationIds( id, clientIds, organizationIds );
        }
        else
            result = documentsRepository.findOne(id);

        if ( result == null )
            return ResponseEntity.status(403).build();

        return ResponseEntity.ok( new DocumentResponse( result ) );

    }

    @PutMapping("/{documentId}/serviceWork/{serviceWorkId}/price")
    public ResponseEntity updateServiceWork(@PathVariable("documentId") Integer documentId,
                                            @PathVariable("serviceWorkId") Integer serviceWorkId,
                                            @RequestParam("price") Double price,
                                            @RequestParam("byPrice") Boolean byPrice) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( !UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) ) return ResponseEntity.status(403).build();

        if ( byPrice )
            serviceWorkRepository.updateServiceWorkByPrice( serviceWorkId, price );
        else
            serviceWorkRepository.updateServiceWorkByPriceNorm( serviceWorkId, price );

        User moderator = null;

        if ( currentUser.getModeratorId() != null )
            moderator = userRepository.findOne( currentUser.getModeratorId() );

        if ( moderator != null )
            eventMessageService.buildServiceWorkChangeMessage( currentUser, moderator, documentId, serviceWorkId, byPrice, price );

        return findOne(documentId);

    }

    @PutMapping("/{documentId}/documentOutHeader/{documentOutHeaderId}/state")
    public ResponseEntity updateDocumentOutHeader(@PathVariable("documentId") Integer documentId,
                                                  @PathVariable("documentOutHeaderId") Integer documentOutHeaderId,
                                                  @RequestParam("state") Integer state) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( !UserHelper.hasRole( currentUser, "MODERATOR" ) ) return ResponseEntity.status(403).build();

        documentOutHeaderRepository.updateState( documentOutHeaderId, state );

        return findOne(documentId);

    }

    @PutMapping("/{documentId}/serviceGoodsAddon/{serviceGoodsAddonId}/cost")
    public ResponseEntity updateServiceGoodsAddon(@PathVariable("documentId") Integer documentId,
                                                  @PathVariable("serviceGoodsAddonId") Integer serviceGoodsAddonId,
                                                  @RequestParam("cost") Double cost) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( !UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) ) return ResponseEntity.status(403).build();

        serviceGoodsAddonRepository.updateCost( serviceGoodsAddonId, cost );

        User moderator = null;

        if ( currentUser.getModeratorId() != null )
            moderator = userRepository.findOne( currentUser.getModeratorId() );

        if ( moderator != null )
            eventMessageService.buildServiceGoodsAddonChangeMessage( currentUser, moderator, documentId, serviceGoodsAddonId, cost );

        return findOne(documentId);

    }

    @GetMapping("/count")
    public ResponseEntity count() {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Integer result = null;

        if ( UserHelper.hasRole(currentUser, "ADMIN") )
            result = documentsRepository.countAll();
        else if ( ( UserHelper.hasRole(currentUser, "MODERATOR") ) ) {

            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            if ( clientIds.size() == 0 )
                result = 0;
            else
                result = documentsRepository.countByClientIds( clientIds );

        }
        else if ( UserHelper.hasRole(currentUser, "CLIENT") ) {

            if ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByClientId( currentUser.getClientId() );

        }
        else if ( UserHelper.hasRole(currentUser, "SERVICE_LEADER") ) {

            if ( currentUser.getOrganizationId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByOrganizationId( currentUser.getOrganizationId() );

        }

        if ( result == null ) return ResponseEntity.ok(0);

        return ResponseEntity.ok( result );

    }

    @GetMapping("/count/state")
    public ResponseEntity countByState(@RequestParam("state") Integer state) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Integer result = null;

        if ( UserHelper.hasRole(currentUser, "ADMIN") )
            result = documentsRepository.countByState(state);
        else if ( ( UserHelper.hasRole(currentUser, "MODERATOR") ) ) {

            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            if ( clientIds.size() == 0 )
                result = 0;
            else
                result = documentsRepository.countByClientIdsAndState( clientIds, state );

        }
        else if ( UserHelper.hasRole(currentUser, "CLIENT") ) {

            if ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByClientIdAndState( currentUser.getClientId(), state );

        }
        else if ( UserHelper.hasRole(currentUser, "SERVICE_LEADER") ) {

            if ( currentUser.getOrganizationId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByOrganizationIdAndState( currentUser.getOrganizationId(), state );

        }

        if ( result == null ) return ResponseEntity.ok(0);

        return ResponseEntity.ok( result );

    }

    @Data
    public static class FilterPayload {

        private List<Integer> organizations;
        private List<Integer> states;
        private List<Integer> vehicles;

    }

}
