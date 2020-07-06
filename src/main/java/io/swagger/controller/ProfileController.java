package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.ProfileResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/external/profiles")
public class ProfileController {

    private final static Logger logger = LoggerFactory.getLogger( ProfileController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileResourceProcessor profileResourceProcessor;

    @GetMapping
    public ResponseEntity findProfiles(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(404).build();

        FilterPayload filterPayload = params.getFilterPayload();
        if ( filterPayload.getPhone() == null || filterPayload.getPhone().length() < 3 ||
                filterPayload.getEmail() == null || filterPayload.getEmail().length() < 3 ||
                filterPayload.getFio() == null || filterPayload.getFio().length() < 3 )
            return ResponseEntity.status(400).build();

        List<Profile> profiles = profileRepository.findAllByPhoneOrEmail(
                String.format("%%%s%%", filterPayload.getPhone()),
                String.format("%%%s%%", filterPayload.getEmail()),
                String.format("%%%s%%", filterPayload.getFio())
        );
        if ( profiles.size() == 0 )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok(
                profileResourceProcessor.toResourceList(
                        profiles,
                        null,
                        (long) profiles.size(),
                        null
                )
        );
    }

    @GetMapping("/clients")
    public ResponseEntity findAllClients() throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isServiceLeader( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(404).build();

        List<Profile> clients;

        if ( UserHelper.isAdmin( currentUser ) )
            clients = profileRepository.findAll();
        else {
            if ( currentUser.getProfile() == null )
                return ResponseEntity.status(404).build();
            clients = profileRepository.findClientsByExecutorId( currentUser.getProfile().getId() );
        }

        return ResponseEntity.ok(
                profileResourceProcessor.toResourceList(
                        clients,
                        null,
                        (long) clients.size(),
                        null
                )
        );
    }

    @GetMapping("/executors")
    public ResponseEntity findAllExecutors() throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isClient( currentUser ) )
            return ResponseEntity.status(404).build();

        List<Profile> clients;

        if ( UserHelper.isAdmin( currentUser ) )
            clients = profileRepository.findExecutors();
        else {
            if ( currentUser.getProfile() == null )
                return ResponseEntity.status(404).build();
            clients = profileRepository.findExecutorsByClientId( currentUser.getProfile().getId() );
        }

        return ResponseEntity.ok(
                profileResourceProcessor.toResourceList(
                        clients,
                        null,
                        (long) clients.size(),
                        null
                )
        );
    }

    @Data
    public static class FilterPayload {
        private String phone;
        private String email;
        private String fio;
    }

    @Data
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("phone") && getFilter().get("phone").size() > 0 )
                filterPayload.setPhone( getFilter().get("phone").get(0) );
            if ( getFilter().containsKey("email") && getFilter().get("email").size() > 0 )
                filterPayload.setEmail( getFilter().get("email").get(0) );
            if ( getFilter().containsKey("fio") && getFilter().get("fio").size() > 0 )
                filterPayload.setFio( getFilter().get("fio").get(0) );

            return filterPayload;
        }
    }
}
