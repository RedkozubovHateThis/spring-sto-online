package io.swagger.api;

import io.swagger.model.RefUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import io.swagger.repository.RefUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RefUserApiController implements RefUserApi {

    private static final Logger log = LoggerFactory.getLogger(RefUserApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private RefUserRepository refUserRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public RefUserApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> createUser(@ApiParam(value = "токен пользователя" ,required=true) @RequestHeader(value="x-request-token", required=true) String xRequestToken,@ApiParam(value = "Создание пользователя" ,required=true )  @Valid @RequestBody RefUser body) {
        String accept = request.getHeader("Accept");

        refUserRepository.save(body);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

//    public ResponseEntity<Void> refUserPut(@ApiParam(value = "Обновление данных о пользователе" ,required=true ) @Valid @RequestBody RefUser body,
//                                           @ApiParam(value = "ид пользователя, которого необходимо обновить") @Valid @RequestParam(value = "id", required = false) Integer id) {
//        String accept = request.getHeader("Accept");
//
//        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
//    }



    public ResponseEntity refUserGet(@ApiParam(value = "токен пользователя" ,required=true) @RequestHeader(value="x-request-token", required=true) String xRequestToken,@ApiParam(value = "id пользователя.") @Valid @RequestParam(value = "id", required = false) Integer id) {
        String accept = request.getHeader("Accept");

        RefUser refUser = refUserRepository.findOne(id);

        if ( refUser != null )
            return ResponseEntity.ok(refUser);
        else
            return ResponseEntity.status(404).build();

    }

}
