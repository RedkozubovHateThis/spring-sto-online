package io.swagger.controller;

import io.swagger.helper.ProfileSpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.ProfileResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
import io.swagger.service.UserService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/external/profiles")
public class ProfileController {

    private final static Logger logger = LoggerFactory.getLogger( ProfileController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileResourceProcessor profileResourceProcessor;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin(currentUser) )
            return ResponseEntity.status(403).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<Profile> specification =
                ProfileSpecificationBuilder.buildSpecification( currentUser, filterPayload );

        return ResponseEntity.ok(
                profileResourceProcessor.toResourcePage(
                        profileRepository.findAll(specification, pageable), params.getInclude(),
                        profileRepository.count(specification), pageable
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity findProfiles(@RequestParam("search") String search) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        if ( search == null || search.length() < 3 )
            return ResponseEntity.status(400).build();

        List<Profile> profiles = profileRepository.findAllByPhoneOrEmail(
                String.format("%%%s%%", search),
                String.format("%%%s%%", search),
                String.format("%%%s%%", search),
                String.format("%%%s%%", search)
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

    @PutMapping("/register")
    public ResponseEntity registerProfile(@RequestParam("profileId") Long profileId,
                                          @RequestParam("roleName") String roleName) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        Optional<Profile> profileOptional = profileRepository.findById(profileId);

        if ( !profileOptional.isPresent() )
            return ResponseEntity.status(404).build();

        userService.generateUser(profileOptional.get(), roleName);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/clients")
    public ResponseEntity findAllClients() throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) && !UserHelper.isAdmin( currentUser ) )
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
        private String inn;
        private String address;
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
            if ( getFilter().containsKey("inn") && getFilter().get("inn").size() > 0 )
                filterPayload.setInn( getFilter().get("inn").get(0) );
            if ( getFilter().containsKey("address") && getFilter().get("address").size() > 0 )
                filterPayload.setAddress( getFilter().get("address").get(0) );

            return filterPayload;
        }
    }
}
