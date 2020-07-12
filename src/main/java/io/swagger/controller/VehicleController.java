package io.swagger.controller;

import io.swagger.helper.ServiceWorkDictionarySpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.helper.VehicleSpecificationBuilder;
import io.swagger.postgres.model.ServiceWorkDictionary;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.*;
import io.swagger.postgres.resourceProcessor.VehicleResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/external/vehicles")
public class VehicleController {

    private final static Logger logger = LoggerFactory.getLogger( VehicleController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleResourceProcessor vehicleResourceProcessor;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(404).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<Vehicle> specification =
                VehicleSpecificationBuilder.buildSpecification( filterPayload );

        return ResponseEntity.ok(
                vehicleResourceProcessor.toResourcePage(
                        vehicleRepository.findAll(specification, pageable), params.getInclude(),
                        vehicleRepository.count(specification), pageable
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity findVehicles(@RequestParam("search") String search) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(404).build();

        if ( search == null || search.length() < 3 )
            return ResponseEntity.status(400).build();

        List<Vehicle> vehicles = vehicleRepository.findAllByVinNumberOrRegNumberOrModelName(
                String.format("%%%s%%", search),
                String.format("%%%s%%", search),
                String.format("%%%s%%", search)
        );
        if ( vehicles.size() == 0 )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok(
                vehicleResourceProcessor.toResourceList(
                        vehicles,
                        null,
                        (long) vehicles.size(),
                        null
                )
        );
    }

    @Data
    public static class FilterPayload {
        private String vinNumber;
        private String regNumber;
        private String modelName;
        private Integer year;
    }

    @Data
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("vinNumber") && getFilter().get("vinNumber").size() > 0 )
                filterPayload.setVinNumber( getFilter().get("vinNumber").get(0) );
            if ( getFilter().containsKey("modelName") && getFilter().get("modelName").size() > 0 )
                filterPayload.setModelName( getFilter().get("modelName").get(0) );
            if ( getFilter().containsKey("regNumber") && getFilter().get("regNumber").size() > 0 )
                filterPayload.setRegNumber( getFilter().get("regNumber").get(0) );
            if ( getFilter().containsKey("year") && getFilter().get("year").size() > 0 )
                filterPayload.setYear( Integer.parseInt( getFilter().get("year").get(0), 10) );

            return filterPayload;
        }
    }
}
