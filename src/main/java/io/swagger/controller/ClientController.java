package io.swagger.controller;

import io.swagger.firebird.model.Client;
import io.swagger.firebird.repository.ClientRepository;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.model.security.User;
import io.swagger.response.firebird.ClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

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

    @GetMapping("/findAll")
    public ResponseEntity findAll() {

        User currentUser = userRepository.findCurrentUser();
        List<Client> clients;

        if ( UserHelper.isAdmin( currentUser ) )
            clients = clientRepository.findAll();
        else if ( UserHelper.isModerator( currentUser ) ) {
            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            if ( clientIds.size() == 0 )
                return ResponseEntity.status(404).build();

            clients = clientRepository.findClientsByIds( clientIds );
        }
        else if ( UserHelper.isServiceLeader( currentUser ) && currentUser.isServiceLeaderValid() )
            clients = clientRepository.findClientsByOrganizationId( currentUser.getOrganizationId() );
        else if ( UserHelper.isFreelancer( currentUser ) && currentUser.isFreelancerValid() )
            clients = clientRepository.findClientsByOrganizationIdAndManagerId( currentUser.getOrganizationId(),
                    currentUser.getManagerId() );
        else
            return ResponseEntity.status(404).build();

        if ( clients.size() == 0 )
            return ResponseEntity.status(404).build();

        List<ClientResponse> responses = clients.stream().map(ClientResponse::new).collect( Collectors.toList() );

        return ResponseEntity.ok( responses );

    }

}
