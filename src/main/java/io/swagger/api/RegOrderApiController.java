package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.RegOrder;
import io.swagger.repository.RegOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RegOrderApiController implements RegOrderApi {


    @Autowired
    RegOrderRepository regOrderRepository;

    private final HttpServletRequest request;

    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public RegOrderApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<Void> regOrderPost(@ApiParam(value = "Создание наряда", required = true) @Valid @RequestBody RegOrder body) {
        String accept = request.getHeader("Accept");

        regOrderRepository.save(body);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity regOrderGet(@ApiParam(value = "id наряда.") @Valid @RequestParam(value = "id", required = false) Integer id) {
        String accept = request.getHeader("Accept");

        RegOrder regOrder = regOrderRepository.findOne(id);

        if (regOrder != null)
            return ResponseEntity.ok(regOrder);
        else
            return ResponseEntity.status(404).build();

    }

}
