package io.swagger.controller;

import io.swagger.firebird.repository.EmployeeRepository;
import io.swagger.firebird.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secured/test")
@RestController
public class TestController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository firebirdUserRepository;

    @GetMapping("/employee/findAll")
    public ResponseEntity findAllEmployee() {

        return ResponseEntity.ok( employeeRepository.findAll() );

    }

    @GetMapping("/users/findAll")
    public ResponseEntity findAllUsers() {

        return ResponseEntity.ok( firebirdUserRepository.findAll() );

    }

}
