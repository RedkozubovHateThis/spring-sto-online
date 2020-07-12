package io.swagger.controller;

import io.swagger.helper.ServiceAddonDictionarySpecificationBuilder;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceAddonDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceAddonDictionaryRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.ServiceAddonDictionaryResourceProcessor;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/external/serviceAddonDictionaries")
public class ServiceAddonDictionaryController {

    private final static Logger logger = LoggerFactory.getLogger( ServiceAddonDictionaryController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceAddonDictionaryRepository serviceAddonDictionaryRepository;
    @Autowired
    private ServiceAddonDictionaryResourceProcessor serviceAddonDictionaryResourceProcessor;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(404).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<ServiceAddonDictionary> specification =
                ServiceAddonDictionarySpecificationBuilder.buildSpecification( filterPayload );

        return ResponseEntity.ok(
                serviceAddonDictionaryResourceProcessor.toResourcePage(
                        serviceAddonDictionaryRepository.findAll(specification, pageable), params.getInclude(),
                        serviceAddonDictionaryRepository.count(specification), pageable
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity findServiceAddonDictionaries(@RequestParam("name") String name) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(404).build();

        if ( name == null || name.length() < 3 )
            return ResponseEntity.status(400).build();

        List<ServiceAddonDictionary> serviceAddonDictionaries = serviceAddonDictionaryRepository.findAllByName(
                String.format("%%%s%%", name)
        );
        if ( serviceAddonDictionaries.size() == 0 )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok(
                serviceAddonDictionaryResourceProcessor.toResourceList(
                        serviceAddonDictionaries,
                        null,
                        (long) serviceAddonDictionaries.size(),
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
