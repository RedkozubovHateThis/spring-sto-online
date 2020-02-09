package io.swagger.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.firebird.repository.DocumentOutHeaderRepository;
import io.swagger.firebird.repository.ServiceGoodsAddonRepository;
import io.swagger.firebird.repository.ServiceWorkRepository;
import io.swagger.helper.DocumentSpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.firebird.DocumentResponse;
import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.service.DocumentServiceDetailService;
import io.swagger.service.EventMessageService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    @Autowired
    private DocumentServiceDetailService documentsService;

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable, FilterPayload filterPayload) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        webSocketController.sendCounterRefreshMessage( currentUser, false, false );

        List<Integer> paidDocumentsIds = new ArrayList<>();

        if ( UserHelper.isClient( currentUser ) &&
                ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) )
            return ResponseEntity.status(404).build();

        else if ( UserHelper.isServiceLeader( currentUser ) &&
                ( !currentUser.isServiceLeaderValid() ||
                        currentUser.getIsAccessRestricted() ) )
            return ResponseEntity.status(404).build();

        else if ( UserHelper.isFreelancer( currentUser ) &&
                ( !currentUser.isFreelancerValid() ||
                        currentUser.getIsAccessRestricted() ) )
            return ResponseEntity.status(404).build();

        Specification<DocumentServiceDetail> specification;

        if ( UserHelper.isModerator( currentUser ) ) {
            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            List<Integer> organizationIds = userRepository.collectOrganizationIds( currentUser.getId() );

            if ( clientIds.size() == 0 && organizationIds.size() == 0 )
                return ResponseEntity.status(404).build();

            specification = DocumentSpecificationBuilder.buildSpecificationList(clientIds, organizationIds, currentUser, filterPayload);
        }
        else if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) ) {

            paidDocumentsIds =
                    documentsService.collectPaidDocumentIds( filterPayload.getFromDate(), filterPayload.getToDate() );

            if ( paidDocumentsIds == null || paidDocumentsIds.size() == 0 )
                return ResponseEntity.status(404).build();

            specification = DocumentSpecificationBuilder.buildSpecificationList(paidDocumentsIds, currentUser, filterPayload);
        }
        else
            specification = DocumentSpecificationBuilder.buildSpecificationList(currentUser, filterPayload);

        Page<DocumentServiceDetail> result = documentsRepository.findAll(specification, pageable);

        List<DocumentServiceDetail> resultList = result.getContent();

        List<DocumentResponse> responseList;
        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) ) {

            responseList = new ArrayList<>();
            Date firstSubscriptionDate = documentsService.getFirstSubscriptionDate(currentUser);

            for (DocumentServiceDetail documentServiceDetail : resultList) {

                boolean notInPaidDocuments = !paidDocumentsIds.contains( documentServiceDetail.getId() );
                boolean afterFirstSubscription =
                        firstSubscriptionDate != null && firstSubscriptionDate.before( documentServiceDetail.getDateStart() );

                responseList.add(
                        new DocumentResponse(
                                documentServiceDetail, notInPaidDocuments && afterFirstSubscription
                        )
                );
            }
        }
        else {
            responseList = resultList.stream()
                    .map(DocumentResponse::new).collect( Collectors.toList() );
        }

        Page<DocumentResponse> responsePage = new PageImpl<>(responseList, pageable, result.getTotalElements());

        return ResponseEntity.ok( responsePage );

    }

    @GetMapping("/eventMessages/findAll")
    public ResponseEntity findEventMessagesDocuments() {

        User currentUser = userRepository.findCurrentUser();
        List<Integer> documentIds = null;

        if ( UserHelper.isAdmin( currentUser ) ) {
            documentIds = userRepository.collectDocumentIdsByAdmin();
        }
        else if ( UserHelper.isModerator( currentUser ) ) {
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

        if ( UserHelper.isClient( currentUser ) ) {
            if ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) return ResponseEntity.status(403).build();

            if ( currentUser.getVinNumbers() != null && currentUser.getVinNumbers().length > 0 )
                result = documentsRepository.findOneByClientIdOrVinNumbers( id, currentUser.getClientId(), Arrays.asList( currentUser.getVinNumbers() ) );
            else
                result = documentsRepository.findOneByClientId( id, currentUser.getClientId() );
        }
        else if ( UserHelper.isServiceLeader( currentUser ) ) {

            if ( !currentUser.isServiceLeaderValid() || currentUser.getIsAccessRestricted() )
                return ResponseEntity.status(403).build();

            List<Integer> paidDocumentsIds = documentsService.collectPaidDocumentIds( null, null );

            if ( !paidDocumentsIds.contains( id ) ) return ResponseEntity.status(403).build();

            result = documentsRepository.findOneByOrganizationId( id, currentUser.getOrganizationId() );
        }
        else if ( UserHelper.isFreelancer( currentUser ) ) {

            if ( !currentUser.isFreelancerValid() || currentUser.getIsAccessRestricted() )
                return ResponseEntity.status(403).build();

            List<Integer> paidDocumentsIds = documentsService.collectPaidDocumentIds( null, null );

            if ( !paidDocumentsIds.contains( id ) ) return ResponseEntity.status(403).build();

            result = documentsRepository.findOneByOrganizationIdAndManagerId( id, currentUser.getOrganizationId(), currentUser.getManagerId() );
        }
        else if ( UserHelper.isModerator( currentUser ) ) {
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

        if ( UserHelper.isClient( currentUser ) ) return ResponseEntity.status(403).build();

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
    public ResponseEntity updateDocumentOutHeaderState(@PathVariable("documentId") Integer documentId,
                                                       @PathVariable("documentOutHeaderId") Integer documentOutHeaderId,
                                                       @RequestParam("state") Integer state) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( UserHelper.isClient( currentUser ) ) return ResponseEntity.status(403).build();

        documentOutHeaderRepository.updateState( documentOutHeaderId, state );

        webSocketController.sendCounterRefreshMessage( currentUser, true, true );

        return findOne(documentId);

    }

    @PutMapping("/{documentId}/documentOutHeader/{documentOutHeaderId}/user")
    public ResponseEntity updateDocumentOutHeaderUser(@PathVariable("documentId") Integer documentId,
                                                      @PathVariable("documentOutHeaderId") Integer documentOutHeaderId,
                                                      @RequestParam("userId") Integer userId) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( !UserHelper.isAdminOrModerator( currentUser ) ) return ResponseEntity.status(403).build();

        documentOutHeaderRepository.updateUserId( documentOutHeaderId, userId );

        return findOne(documentId);

    }

    @PutMapping("/{documentId}/documentOutHeader/{documentOutHeaderId}/manager")
    public ResponseEntity updateDocumentOutHeaderManager(@PathVariable("documentId") Integer documentId,
                                                         @PathVariable("documentOutHeaderId") Integer documentOutHeaderId,
                                                         @RequestParam("managerId") Integer managerId) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( !UserHelper.isAdminOrModerator( currentUser ) ) return ResponseEntity.status(403).build();

        documentOutHeaderRepository.updateManagerId( documentOutHeaderId, managerId );

        return findOne(documentId);

    }

    @PutMapping("/{documentId}/serviceGoodsAddon/{serviceGoodsAddonId}/cost")
    public ResponseEntity updateServiceGoodsAddon(@PathVariable("documentId") Integer documentId,
                                                  @PathVariable("serviceGoodsAddonId") Integer serviceGoodsAddonId,
                                                  @RequestParam("cost") Double cost) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( UserHelper.isClient( currentUser ) ) return ResponseEntity.status(403).build();

        serviceGoodsAddonRepository.updateCost( serviceGoodsAddonId, cost );

        User moderator = null;

        if ( currentUser.getModeratorId() != null )
            moderator = userRepository.findOne( currentUser.getModeratorId() );

        if ( moderator != null )
            eventMessageService.buildServiceGoodsAddonChangeMessage( currentUser, moderator, documentId, serviceGoodsAddonId, cost );

        return findOne(documentId);

    }

    @Data
    public static class FilterPayload {

        private List<Integer> organizations;
        private List<Integer> states;
        private String vehicle;
        private String vinNumber;
        private List<Integer> clients;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        private Date fromDate;
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
        private Date toDate;
        private DocumentPaymentState paymentState;

    }

    public static enum DocumentPaymentState {
        PAID,
        NOT_PAID
    }

}
