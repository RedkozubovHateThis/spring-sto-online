package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.helper.VehicleDictionarySpecificationBuilder;
import io.swagger.postgres.model.VehicleDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.VehicleDictionaryRepository;
import io.swagger.postgres.resourceProcessor.VehicleDictionaryResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/external/vehicleDictionaries")
public class VehicleDictionaryController {

    private final static Logger logger = LoggerFactory.getLogger( VehicleDictionaryController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleDictionaryRepository vehicleDictionaryRepository;
    @Autowired
    private VehicleDictionaryResourceProcessor vehicleDictionaryResourceProcessor;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<VehicleDictionary> specification =
                VehicleDictionarySpecificationBuilder.buildSpecification( filterPayload );

        return ResponseEntity.ok(
                vehicleDictionaryResourceProcessor.toResourcePage(
                        vehicleDictionaryRepository.findAll(specification, pageable), params.getInclude(),
                        vehicleDictionaryRepository.count(specification), pageable
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity findVehicleDictionaries(@RequestParam("name") String name) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.status(404).build();

        if ( name == null || name.length() < 3 )
            return ResponseEntity.status(400).build();

        List<VehicleDictionary> vehicleDictionaries = vehicleDictionaryRepository.findAllByName(
                String.format("%%%s%%", name)
        );
        if ( vehicleDictionaries.size() == 0 )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok(
                vehicleDictionaryResourceProcessor.toResourceList(
                        vehicleDictionaries,
                        null,
                        (long) vehicleDictionaries.size(),
                        null
                )
        );
    }

    @Data
    public static class FilterPayload {
        private String name;
    }

    @Data
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("name") && getFilter().get("name").size() > 0 )
                filterPayload.setName( getFilter().get("name").get(0) );

            return filterPayload;
        }
    }
}
