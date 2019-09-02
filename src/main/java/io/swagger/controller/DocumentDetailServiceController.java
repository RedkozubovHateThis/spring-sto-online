package io.swagger.controller;

import io.swagger.firebird.repository.DocumentOutHeaderRepository;
import io.swagger.firebird.repository.ServiceWorkRepository;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.DocumentResponse;
import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private UserRepository userRepository;

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Page<DocumentServiceDetail> result = null;

        if ( UserHelper.hasRole(currentUser, "ADMIN") )
            result = documentsRepository.findAll(pageable);
        else if ( ( UserHelper.hasRole(currentUser, "MODERATOR") ) ) {

            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            result = documentsRepository.findByClientIds( clientIds, pageable );

        }
        else if ( UserHelper.hasRole(currentUser, "CLIENT") ) {

            if ( currentUser.getClientId() == null ) return ResponseEntity.status(403).build();
            result = documentsRepository.findByClientId( currentUser.getClientId(), pageable );

        }
        else if ( UserHelper.hasRole(currentUser, "SERVICE_LEADER") ) {

            if ( currentUser.getOrganizationId() == null ) return ResponseEntity.status(403).build();
            result = documentsRepository.findByOrganizationId( currentUser.getOrganizationId(), pageable );

        }

        if ( result == null ) return ResponseEntity.status(404).build();

        List<DocumentServiceDetail> resultList = result.getContent();
        List<DocumentResponse> responseList = resultList.stream()
                .map(DocumentResponse::new).collect( Collectors.toList() );

        Page<DocumentResponse> responsePage = new PageImpl<>(responseList, pageable, result.getTotalElements());

        return ResponseEntity.ok( responsePage );

    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Integer id) {

        DocumentServiceDetail result = documentsRepository.findOne(id);

        if ( result == null )
            return ResponseEntity.status(404).build();

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

}
