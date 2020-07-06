package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceWorkDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.ServiceWorkDictionaryRepository;
import io.swagger.postgres.resourceProcessor.ServiceWorkDictionaryResourceProcessor;
import io.swagger.response.api.JsonApiParamsBase;
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
@RequestMapping("/external/serviceWorkDictionaries")
public class ServiceWorkDictionaryController {

    private final static Logger logger = LoggerFactory.getLogger( ServiceWorkDictionaryController.class );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceWorkDictionaryRepository serviceWorkDictionaryRepository;
    @Autowired
    private ServiceWorkDictionaryResourceProcessor serviceWorkDictionaryResourceProcessor;

    @GetMapping
    public ResponseEntity findServiceWorkDictionaries(JsonApiParams params) throws Exception {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) && !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.status(404).build();

        FilterPayload filterPayload = params.getFilterPayload();
        if ( filterPayload.getName() == null || filterPayload.getName().length() < 3 )
            return ResponseEntity.status(400).build();

        List<ServiceWorkDictionary> serviceWorkDictionaries = serviceWorkDictionaryRepository.findAllByName(
                String.format("%%%s%%", filterPayload.getName())
        );
        if ( serviceWorkDictionaries.size() == 0 )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok(
                serviceWorkDictionaryResourceProcessor.toResourceList(
                        serviceWorkDictionaries,
                        null,
                        (long) serviceWorkDictionaries.size(),
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
