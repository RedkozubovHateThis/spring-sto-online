package io.swagger.controller;

import io.swagger.helper.ServiceDocumentSpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.ServiceDocumentResourceProcessor;
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
    public static class JsonApiParams {
        private Map<String, List<String>> filter;
        private List<String> sort;
        private List<String> include;
        private Map<String, Integer> page;

        public FilterPayload getFilterPayload() throws ParseException {
            FilterPayload filterPayload = new FilterPayload();

            if ( filter == null )
                return filterPayload;

            if ( filter.containsKey("state") && filter.get("state").size() > 0 )
                filterPayload.setState( ServiceDocumentStatus.valueOf( filter.get("state").get(0) ) );
            if ( filter.containsKey("organization") && filter.get("organization").size() > 0 )
                filterPayload.setOrganization( Long.parseLong( filter.get("organization").get(0), 10 ) );
            if ( filter.containsKey("vehicle") && filter.get("vehicle").size() > 0 )
                filterPayload.setVehicle( filter.get("vehicle").get(0) );
            if ( filter.containsKey("vinNumber") && filter.get("vinNumber").size() > 0 )
                filterPayload.setVinNumber( filter.get("vinNumber").get(0) );
            if ( filter.containsKey("client") && filter.get("client").size() > 0 )
                filterPayload.setClient( Long.parseLong( filter.get("client").get(0), 10 ) );
            if ( filter.containsKey("fromDate") && filter.get("fromDate").size() > 0 )
                filterPayload.setFromDate( DATE_FORMAT.parse( filter.get("fromDate").get(0) ) );
            if ( filter.containsKey("toDate") && filter.get("toDate").size() > 0 )
                filterPayload.setFromDate( DATE_FORMAT.parse( filter.get("toDate").get(0) ) );

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
