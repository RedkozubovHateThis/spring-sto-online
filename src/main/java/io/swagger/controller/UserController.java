package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
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

        User replacementModerator = user.getReplacementModerator();
        if ( replacementModerator != null ) {

            User origReplacementModerator = userRepository.findOne( replacementModerator.getId() );
            if ( origReplacementModerator != null ) user.setReplacementModerator( origReplacementModerator );
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

}
