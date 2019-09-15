package io.swagger.controller;

import io.swagger.firebird.model.Client;
import io.swagger.firebird.repository.ClientRepository;
import io.swagger.response.firebird.ClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
