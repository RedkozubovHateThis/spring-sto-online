package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.UserResourceProcessor;
import io.swagger.response.api.ApiResponse;
import io.swagger.response.integration.IntegrationDocument;
import io.swagger.response.integration.errors.IntegrationError;
import io.swagger.response.integration.errors.IntegrationErrors;
import io.swagger.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integration")
public class IntegrationController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder userPasswordEncoder;
    @Autowired
    private IntegrationService integrationService;

    @PostMapping(value = "/serviceDocuments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postServiceDocument(@RequestHeader(value = "username", required = false) String username,
                                              @RequestHeader(value = "password", required = false) String password,
                                              @RequestBody IntegrationDocument document) throws Exception {

        User user;

        try {
            user = getAuthentication(username, password);
        }
        catch(IllegalAccessException iae) {
            return ResponseEntity.status(401).body( buildError( HttpStatus.UNAUTHORIZED, iae.getMessage(), "serviceDocuments" ) );
        }

        try {
            integrationService.processIntegrationDocument(document, user);
            return ResponseEntity.ok().build();
        }
        catch(IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( buildError( HttpStatus.BAD_REQUEST, iae.getMessage(), "serviceDocuments" ) );
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( buildError( HttpStatus.BAD_REQUEST, e.getMessage(), "serviceDocuments" ) );
        }

    }

    private User getAuthentication(String username, String password) throws IllegalAccessException {
        if ( username == null || username.length() == 0 || password == null || password.length() == 0 )
            throw new IllegalAccessException("Не указаны логин или пароль");

        User user = userRepository.findByUsername(username);
        if ( user == null )
            throw new IllegalAccessException("Указанный пользователь не найден");

        if ( !UserHelper.isAdmin( user ) && !UserHelper.isServiceLeaderOrFreelancer( user ) )
            throw new IllegalAccessException("Указанный пользователь не может создавать/изменять заказ-наряды");

        if ( !userPasswordEncoder.matches( password, user.getPassword() ) )
            throw new IllegalAccessException("Указанный пароль неверен");

        return user;
    }

    private IntegrationErrors buildError(HttpStatus status, String detail, String path) {
        return new IntegrationErrors(status, detail, "/integration/" + path);
    }

}
