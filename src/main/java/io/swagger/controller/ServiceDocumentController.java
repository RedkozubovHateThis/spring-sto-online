package io.swagger.controller;

import io.swagger.helper.ServiceDocumentSpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.ServiceDocumentResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/external/serviceDocuments")
@RestController
public class ServiceDocumentController {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;

    @Autowired
    private ServiceDocumentResourceProcessor serviceDocumentResourceProcessor;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin(currentUser) && currentUser.getProfile() == null )
            return ResponseEntity.status(404).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<ServiceDocument> specification =
                ServiceDocumentSpecificationBuilder.buildSpecification( currentUser, filterPayload );

        return ResponseEntity.ok(
                serviceDocumentResourceProcessor.toResourcePage(
                        serviceDocumentRepository.findAll(specification, pageable), params.getInclude(),
                        serviceDocumentRepository.count(specification), pageable
                )
        );
    }

    @Data
    public static class FilterPayload {

        private ServiceDocumentStatus state;
        private Long organization;
        private String vehicle;
        private String vinNumber;
        private Long client;
        private Date fromDate;
        private Date toDate;

    }

    @Data
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() throws Exception {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("state") && getFilter().get("state").size() > 0 )
                filterPayload.setState( ServiceDocumentStatus.valueOf( getFilter().get("state").get(0) ) );
            if ( getFilter().containsKey("organization") && getFilter().get("organization").size() > 0 )
                filterPayload.setOrganization( Long.parseLong( getFilter().get("organization").get(0), 10 ) );
            if ( getFilter().containsKey("vehicle") && getFilter().get("vehicle").size() > 0 )
                filterPayload.setVehicle( getFilter().get("vehicle").get(0) );
            if ( getFilter().containsKey("vinNumber") && getFilter().get("vinNumber").size() > 0 )
                filterPayload.setVinNumber( getFilter().get("vinNumber").get(0) );
            if ( getFilter().containsKey("client") && getFilter().get("client").size() > 0 )
                filterPayload.setClient( Long.parseLong( getFilter().get("client").get(0), 10 ) );
            if ( getFilter().containsKey("fromDate") && getFilter().get("fromDate").size() > 0 )
                filterPayload.setFromDate( DATE_FORMAT.parse( getFilter().get("fromDate").get(0) ) );
            if ( getFilter().containsKey("toDate") && getFilter().get("toDate").size() > 0 )
                filterPayload.setFromDate( DATE_FORMAT.parse( getFilter().get("toDate").get(0) ) );

            return filterPayload;
        }
    }

}
