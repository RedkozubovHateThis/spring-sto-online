package io.swagger.controller;

import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/oauth")
@RestController
public class oAuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody User user) {

        if ( user == null )
            return ResponseEntity.status(400).body("Тело запроса не может быть пустым.");

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
        user.setPassword( userPasswordEncoder.encode( user.getPassword() ) );

        UserRole clientRole = userRoleRepository.findByName("CLIENT");

        if ( clientRole != null )
            user.getRoles().add(clientRole);

        userRepository.save(user);

        return ResponseEntity.ok().build();


    }

}
