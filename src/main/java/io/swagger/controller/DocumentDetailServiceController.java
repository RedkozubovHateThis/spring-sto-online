package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.FirebirdResponse;
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
    private UserRepository userRepository;

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Page<DocumentServiceDetail> result = null;

        if ( UserHelper.hasRole("ADMIN") )
            result = documentsRepository.findAll(pageable);
        else if ( UserHelper.hasRole("CLIENT") ) {

            if ( currentUser.getClientId() == null ) return ResponseEntity.status(403).build();
            result = documentsRepository.findByClientId( currentUser.getClientId(), pageable );

        }

        if ( result == null ) return ResponseEntity.status(403).build();

        List<DocumentServiceDetail> resultList = result.getContent();
        List<FirebirdResponse> responseList = resultList.stream()
                .map(FirebirdResponse::new).collect( Collectors.toList() );

        Page<FirebirdResponse> responsePage = new PageImpl<>(responseList, pageable, result.getTotalElements());

        return ResponseEntity.ok( responsePage );

    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Integer id) {

        DocumentServiceDetail result = documentsRepository.findOne(id);

        if ( result == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new FirebirdResponse( result ) );

    }

}
