package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.EventMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/secured/users")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private EventMessageService eventMessageService;

    @GetMapping("/currentUser")
    public ResponseEntity getCurrentUser() {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        return ResponseEntity.ok( currentUser );

    }

    @PutMapping("{id}")
    public ResponseEntity saveExistingUser(@RequestBody User user,
                                           @PathVariable("id") Long id) {

        User existingUser = userRepository.findOne(id);

        if ( existingUser == null )
            return ResponseEntity.status(404).body("Пользователь не найден");

        User currentUser = userRepository.findCurrentUser();
        boolean sendMessage = false;
        User targetUser = null;

        Long replacementModeratorId = user.getCurrentReplacementModeratorId();
        if ( replacementModeratorId != null ) {

            User origReplacementModerator = userRepository.findOne( replacementModeratorId );
            if ( origReplacementModerator != null ) {

                User existingReplacementModerator = existingUser.getReplacementModerator();

                if ( existingReplacementModerator == null ||
                        !existingReplacementModerator.getId().equals( origReplacementModerator.getId() ) ) {
                    sendMessage = true;
                    targetUser = origReplacementModerator;
                }

                user.setReplacementModerator( origReplacementModerator );
            }
            else user.setReplacementModerator( null );

        }

        user.setPassword( existingUser.getPassword() );
        user.setAccountExpired( existingUser.isAccountExpired() );
        user.setAccountLocked( existingUser.isAccountLocked() );
        user.setCredentialsExpired( existingUser.isCredentialsExpired() );
        user.setDocumentUserState( existingUser.getDocumentUserState() );
        userRepository.save( user );

        if ( user.getModeratorId() != null )
            webSocketController.sendCounterRefreshMessage( user.getModeratorId() );

        if ( sendMessage && targetUser != null ) {
            eventMessageService.buildModeratorReplacementMessage( currentUser, targetUser );
        }

        return ResponseEntity.ok(user);

    }

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) )
            return ResponseEntity.ok( userRepository.findAll(pageable) );
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            return ResponseEntity.ok( userRepository.findAllByModeratorId(currentUser.getId(), pageable) );
        }

        return ResponseEntity.status(403).build();

    }

    @GetMapping("/findReplacementModerators")
    public ResponseEntity findReplacementModerators() {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();
        if ( !UserHelper.hasRole( currentUser, "MODERATOR" ) ) return ResponseEntity.status(404).build();

        return ResponseEntity.ok( userRepository.findUsersByRoleNameExceptId( "MODERATOR", currentUser.getId() ) );

    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Long id) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        User user = userRepository.findOne(id);

        if ( user == null )
            return ResponseEntity.status(404).build();

        if ( currentUser.getId().equals( user.getId() ) ||
                UserHelper.hasRole(currentUser, "ADMIN") ||
                ( UserHelper.hasRole( currentUser, "MODERATOR" ) && user.getModeratorId() != null && user.getModeratorId().equals( currentUser.getId() ) ) )
            return ResponseEntity.ok( user );
        else
            return ResponseEntity.status(403).build();
    }

    @GetMapping("/count")
    public ResponseEntity count(@RequestParam("notApprovedOnly") Boolean notApprovedOnly) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Long result = null;

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
            if ( notApprovedOnly )
                result = userRepository.countAllNotApproved();
            else
                result = userRepository.countAll();
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            if ( notApprovedOnly )
                result = userRepository.countAllNotApprovedByModeratorId(currentUser.getId());
            else
                result = userRepository.countAllByModeratorId(currentUser.getId());
        }

        if ( result == null ) return ResponseEntity.ok(0);

        return ResponseEntity.ok(result);

    }

    @GetMapping("/eventMessages/fromUsers")
    public ResponseEntity findEventMessagesFromUsers() {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageFromUsersByAdmin() );
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageFromUsers( currentUser.getId() ) );
        }
        else
            return ResponseEntity.status(404).build();

    }

    @GetMapping("/eventMessages/toUsers")
    public ResponseEntity findEventMessagesToUsers() {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageToUsersByAdmin() );
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageToUsers( currentUser.getId() ) );
        }
        else
            return ResponseEntity.status(404).build();

    }

}
