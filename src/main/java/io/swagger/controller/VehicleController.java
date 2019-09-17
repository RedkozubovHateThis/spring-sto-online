package io.swagger.controller;

import io.swagger.firebird.model.Model;
import io.swagger.firebird.model.ModelLink;
import io.swagger.firebird.repository.ModelRepository;
import io.swagger.response.firebird.VehicleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/secured/vehicles")
@RestController
public class VehicleController {

    @Autowired
    private ModelRepository modelRepository;

    @GetMapping("findAll")
    public ResponseEntity findAll() {

        List<Model> result = modelRepository.findAllUsed();
        List<VehicleResponse> responses = result.stream().map(VehicleResponse::new)
                .sorted(Comparator.comparing(VehicleResponse::getName)).collect(Collectors.toList());

        return ResponseEntity.ok( responses );

    }

}
