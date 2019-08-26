package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/secured/users")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/currentUser")
    public ResponseEntity getCurrentUser() {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(404).build();

        return ResponseEntity.ok( currentUser );

    }

    @PutMapping("{id}")
    public ResponseEntity saveExistingUser(@RequestBody User user,
                                           @PathVariable("id") Long id) {

        User existingUser = userRepository.findOne(id);

        if ( existingUser == null )
            return ResponseEntity.status(404).body("Пользователь не найден");

        user.setPassword( existingUser.getPassword() );
        user.setAccountExpired( existingUser.isAccountExpired() );
        user.setAccountLocked( existingUser.isAccountLocked() );
        user.setCredentialsExpired( existingUser.isCredentialsExpired() );
        userRepository.save( user );

        return ResponseEntity.ok(user);

    }

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable) {

        return ResponseEntity.ok( userRepository.findAll(pageable) );

    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Long id) {

        User user = userRepository.findOne(id);

        if ( user == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( user );

    }

}
