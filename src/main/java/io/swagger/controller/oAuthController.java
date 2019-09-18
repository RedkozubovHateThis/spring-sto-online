package io.swagger.controller;

import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestMapping("/oauth")
@RestController
public class oAuthController {

    private static final Logger logger = LoggerFactory.getLogger(oAuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private WebSocketController webSocketController;

    @PostMapping("/register/{roleName}")
    public ResponseEntity register(@RequestBody User user,
                                   @PathVariable("roleName") String roleName) {

        if ( user == null )
            return ResponseEntity.status(400).body("Тело запроса не может быть пустым.");

        if ( roleName == null )
            return ResponseEntity.status(400).body("Роль не может быть пустой.");

        if ( userRepository.isUserExistsPhone( user.getPhone() ) )
            return ResponseEntity.status(400).body("Пользователь с таким телефоном уже существует.");

        if ( userRepository.isUserExistsEmail( user.getEmail() ) )
            return ResponseEntity.status(400).body("Пользователь с такой почтой уже существует.");

        if ( user.getPassword() == null || user.getPassword().isEmpty() )
            return ResponseEntity.status(400).body("Пароль не может быть пустым.");

        if ( user.getEmail() == null || user.getEmail().isEmpty() )
            return ResponseEntity.status(400).body("Почта не может быть пустой.");

        if ( user.getPhone() == null || user.getPhone().isEmpty() )
            return ResponseEntity.status(400).body("Телефон не может быть пустым.");

        if ( user.getUsername() == null || user.getUsername().isEmpty() )
            user.setUsername( UUID.randomUUID().toString() );

        user.setEnabled(true);
        user.setIsApproved(false);
        user.setPassword( userPasswordEncoder.encode( user.getPassword() ) );

        UserRole clientRole = userRoleRepository.findByName(roleName);

        if ( clientRole != null )
            user.getRoles().add(clientRole);

        if ( roleName.equals("CLIENT") || roleName.equals("SERVICE_LEADER") )
            setModerator(user);

        userRepository.save(user);

        return ResponseEntity.ok().build();

    }

    private void setModerator(User user) {

        List<User> moderators = userRepository.findUsersByRoleName("MODERATOR");

        User userModerator = null;

        for (User moderator : moderators) {

            if ( userModerator == null )
                userModerator = moderator;
            else {

                if ( moderator.getLastUserAcceptDate() == null ) {
                    userModerator = moderator;
                    break;
                }
                else if ( moderator.getLastUserAcceptDate().before( userModerator.getLastUserAcceptDate() ) ) {
                    userModerator = moderator;
                }

            }

        }

        if ( userModerator != null ) {
            logger.info( "Got moderator \"{}\" for user \"{}\"", userModerator.getFio(), user.getFio() );
            user.setModeratorId( userModerator.getId() );
            webSocketController.sendCounterRefreshMessage( userModerator.getId() );

            userModerator.setLastUserAcceptDate( new Date() );
            userRepository.save( userModerator );
        }
        else {
            logger.error("Moderator not found");
        }

    }

}
