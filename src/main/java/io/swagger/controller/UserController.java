package io.swagger.controller;

import io.swagger.model.security.User;
import io.swagger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/secured/users")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/currentUser")
    public ResponseEntity getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ( auth == null ) return ResponseEntity.status(401).build();

        User currentUser = (User) auth.getPrincipal();

        if ( currentUser == null ) return ResponseEntity.status(404).build();

        return ResponseEntity.ok( currentUser );

    }

}
