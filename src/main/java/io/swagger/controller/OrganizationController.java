package io.swagger.controller;

import io.swagger.firebird.model.Organization;
import io.swagger.firebird.repository.OrganizationRepository;
import io.swagger.response.firebird.OrganizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/secured/organizations")
@RestController
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Integer id) {

        Organization result = organizationRepository.findOne(id);

        if ( result == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new OrganizationResponse( result ) );

    }

    @GetMapping("/inn/{inn}")
    public ResponseEntity findOne(@PathVariable("inn") String inn) {

        Organization result = organizationRepository.findOrganizationByInn(inn);

        if ( result == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new OrganizationResponse( result ) );

    }

    @GetMapping("findAll")
    public ResponseEntity findAll() {

        List<Organization> result = organizationRepository.findAll();
        List<OrganizationResponse> responses = result.stream().map( OrganizationResponse::new )
                .collect( Collectors.toList() );

        return ResponseEntity.ok( responses );

    }

}
