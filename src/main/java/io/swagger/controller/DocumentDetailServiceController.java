package io.swagger.controller;

import io.swagger.response.FirebirdResponse;
import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable) {

        Page<DocumentServiceDetail> result = documentsRepository.findAll(pageable);
        List<DocumentServiceDetail> resultList = result.getContent();
        List<FirebirdResponse> responseList = resultList.stream()
                .map(FirebirdResponse::new).collect( Collectors.toList() );

        return ResponseEntity.ok( responseList );

    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Integer id) {

        DocumentServiceDetail result = documentsRepository.findOne(id);

        if ( result == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new FirebirdResponse( result ) );

    }

}