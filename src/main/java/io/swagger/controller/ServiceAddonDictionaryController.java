package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceAddonDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceAddonDictionaryRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.ServiceAddonDictionaryResourceProcessor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity findServiceAddonDictionaries(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(404).build();

        FilterPayload filterPayload = params.getFilterPayload();
        if ( filterPayload.getName() == null || filterPayload.getName().length() < 3 )
            return ResponseEntity.status(400).build();

        List<ServiceAddonDictionary> serviceAddonDictionaries = serviceAddonDictionaryRepository.findAllByName(
                String.format("%%%s%%", filterPayload.getName())
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
    public static class JsonApiParams {
        private Map<String, List<String>> filter;
        private List<String> sort;
        private List<String> include;
        private Map<String, Integer> page;

        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( filter == null )
                return filterPayload;

            if ( filter.containsKey("name") && filter.get("name").size() > 0 )
                filterPayload.setName( filter.get("name").get(0) );

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