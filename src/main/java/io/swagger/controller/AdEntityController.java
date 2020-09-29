package io.swagger.controller;

import io.swagger.helper.AdEntitySpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.helper.VehicleDictionarySpecificationBuilder;
import io.swagger.postgres.model.AdEntity;
import io.swagger.postgres.model.VehicleDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.AdEntityRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.VehicleDictionaryRepository;
import io.swagger.postgres.resourceProcessor.AdEntityResourceProcessor;
import io.swagger.postgres.resourceProcessor.VehicleDictionaryResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.service.SchedulerService;
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
@RequestMapping("/external/adEntities")
public class AdEntityController {

    private final static Logger logger = LoggerFactory.getLogger( AdEntityController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdEntityRepository adEntityRepository;
    @Autowired
    private AdEntityResourceProcessor adEntityResourceProcessor;
    @Autowired
    private SchedulerService schedulerService;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<AdEntity> specification =
                AdEntitySpecificationBuilder.buildSpecification( filterPayload );

        return ResponseEntity.ok(
                adEntityResourceProcessor.toResourcePage(
                        adEntityRepository.findAll(specification, pageable), params.getInclude(),
                        adEntityRepository.count(specification), pageable
                )
        );
    }

    @GetMapping("/current")
    public ResponseEntity getCurrentAd() {
        try {
            return ResponseEntity.ok(
                    adEntityResourceProcessor.toResource(
                            schedulerService.getCurrentAdEntity(),
                            null
                    )
            );
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


    @Data
    public static class FilterPayload {
        private String name;
        private String phone;
        private String email;
        private Boolean active;
        private Boolean sideOffer;
        private Long userId;
    }

    @Data
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("name") && getFilter().get("name").size() > 0 )
                filterPayload.setName( getFilter().get("name").get(0) );
            if ( getFilter().containsKey("phone") && getFilter().get("phone").size() > 0 )
                filterPayload.setPhone( getFilter().get("phone").get(0) );
            if ( getFilter().containsKey("email") && getFilter().get("email").size() > 0 )
                filterPayload.setEmail( getFilter().get("email").get(0) );
            if ( getFilter().containsKey("active") && getFilter().get("active").size() > 0 )
                filterPayload.setActive( Boolean.valueOf( getFilter().get("active").get(0) ) );
            if ( getFilter().containsKey("sideOffer") && getFilter().get("sideOffer").size() > 0 )
                filterPayload.setSideOffer( Boolean.valueOf( getFilter().get("sideOffer").get(0) ) );
            if ( getFilter().containsKey("userId") && getFilter().get("userId").size() > 0 )
                filterPayload.setUserId( Long.parseLong( getFilter().get("userId").get(0), 10 ) );

            return filterPayload;
        }
    }
}
