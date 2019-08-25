package io.swagger.controller;

import io.swagger.firebird.model.Client;
import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.repository.ClientRepository;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.response.ClientResponse;
import io.swagger.response.FirebirdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/secured/clients")
@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Integer id) {

        Client result = clientRepository.findOne(id);

        if ( result == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new ClientResponse( result ) );

    }

    @GetMapping("/vin/{vinNumber}")
    public ResponseEntity findOne(@PathVariable("vinNumber") String vinNumber) {

        Client result = clientRepository.findClientByVinNumber(vinNumber);

        if ( result == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new ClientResponse( result ) );

    }

}
