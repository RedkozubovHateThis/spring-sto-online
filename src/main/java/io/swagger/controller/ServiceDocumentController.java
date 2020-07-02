package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.VehicleRepository;
import io.swagger.postgres.resourceProcessor.SimpleResourceProcessor;
import io.swagger.postgres.resourceProcessor.VehicleResourceProcessor;
import io.swagger.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/external/serviceDocuments")
@RestController
public class ServiceDocumentController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleResourceProcessor vehicleResourceProcessor;

    @GetMapping("/previousVehicles")
    public ResponseEntity getTest() throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.notFound().build();

        List<Vehicle> previousVehicles;

        if ( UserHelper.isAdmin( currentUser ) )
            previousVehicles = vehicleRepository.findAll();
        else
            previousVehicles = vehicleRepository.findPreviousVehicles( currentUser.getId() );

        return ResponseEntity.ok(
                vehicleResourceProcessor.toResourceList(
                        previousVehicles,
                        null,
                        (long) previousVehicles.size(),
                        null
                )
        );
    }

}
