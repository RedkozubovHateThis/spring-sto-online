package io.swagger.controller;

import io.swagger.model.security.User;
import io.swagger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/oauth")
@RestController
public class oauthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {

        if ( user == null )
            return ResponseEntity.status(400).body("Тело запроса не может быть пустым.");

        if ( userRepository.isUserExists( user.getUsername() ) )
            return ResponseEntity.status(400).body("Пользователь с таким логином уже существует.");

        if ( user.getPassword() == null || user.getPassword().isEmpty() )
            return ResponseEntity.status(400).body("Пароль не может быть пустым.");

        user.setEnabled(true);
        user.setPassword( userPasswordEncoder.encode( user.getPassword() ) );

        userRepository.save(user);

        return ResponseEntity.ok().build();


    }

    @GetMapping("/currentUser")
    public ResponseEntity getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok().build();

    }

}
