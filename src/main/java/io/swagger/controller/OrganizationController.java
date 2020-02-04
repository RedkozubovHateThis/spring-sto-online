package io.swagger.controller;

import io.swagger.firebird.model.Manager;
import io.swagger.firebird.model.Organization;
import io.swagger.firebird.repository.ManagerRepository;
import io.swagger.firebird.repository.OrganizationRepository;
import io.swagger.response.firebird.ManagerResponse;
import io.swagger.response.firebird.OrganizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/secured/organizations")
@RestController
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ManagerRepository managerRepository;

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

    @GetMapping("/managers/{managerId}")
    public ResponseEntity findOneManager(@PathVariable("managerId") Integer managerId) {

        Manager result = managerRepository.findOne(managerId);

        if ( result == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( new ManagerResponse( result ) );

    }

    @GetMapping("/managers/findAll")
    public ResponseEntity findAllManagers(@RequestParam("organizationId") Integer organizationId) {

        List<Manager> result = managerRepository.findByOrganizationId(organizationId);
        List<ManagerResponse> responses = result.stream().map( ManagerResponse::new )
                .collect( Collectors.toList() );

        return ResponseEntity.ok( responses );

    }

}
