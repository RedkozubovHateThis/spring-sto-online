package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.*;
import io.swagger.postgres.resourceProcessor.VehicleResourceProcessor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity findVehicles(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(403).build();

        FilterPayload filterPayload = params.getFilterPayload();
        if ( filterPayload.getVinNumber() == null || filterPayload.getVinNumber().length() == 0 ||
                filterPayload.getModelName() == null || filterPayload.getModelName().length() == 0 ||
                filterPayload.getRegNumber() == null || filterPayload.getRegNumber().length() == 0 )
            return ResponseEntity.status(400).build();

        List<Vehicle> vehicles = vehicleRepository.findAllByVinNumberOrRegNumberOrModelName(
                String.format("%%%s%%", filterPayload.getVinNumber()),
                String.format("%%%s%%", filterPayload.getRegNumber()),
                String.format("%%%s%%", filterPayload.getModelName())
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
    }

    @Data
    public static class JsonApiParams {
        private Map<String, List<String>> filter;
        private List<String> sort;
        private List<String> include;
        private Map<String, Integer> page;

        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( filter == null )
                return filterPayload;

            if ( filter.containsKey("vinNumber") && filter.get("vinNumber").size() > 0 )
                filterPayload.setVinNumber( filter.get("vinNumber").get(0) );
            if ( filter.containsKey("modelName") && filter.get("modelName").size() > 0 )
                filterPayload.setModelName( filter.get("modelName").get(0) );
            if ( filter.containsKey("regNumber") && filter.get("regNumber").size() > 0 )
                filterPayload.setRegNumber( filter.get("regNumber").get(0) );

            return filterPayload;
        }

        public PageRequest getPageable() {
            int number;
            int size = page.getOrDefault("size", 20);

            if ( !page.containsKey("number") )
                number = 0;
            else
                number = page.get("number") - 1;

            if ( sort == null || sort.size() == 0 )
                return PageRequest.of(number, size);

            String firstField = sort.get(0);
            Sort sortDomain;

            if (firstField.startsWith("-")) {
                List<String> sortFixed = sort.stream().map( eachSort -> eachSort.replaceFirst("-", "") ).collect( Collectors.toList() );
                sortDomain = Sort.by(Sort.Direction.DESC, sortFixed.toArray( new String[ sort.size() ] ) );
            }
            else
                sortDomain = Sort.by(Sort.Direction.ASC, sort.toArray( new String[ sort.size() ] ));

            return PageRequest.of(number, size, sortDomain);
        }
    }
}
