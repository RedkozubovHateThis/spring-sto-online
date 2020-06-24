package io.swagger.controller;

import io.swagger.helper.EventMessageSpecificationBuilder;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.EventMessageResourceProcessor;
import io.swagger.response.EventMessageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/external/eventMessages")
@RestController
public class EventMessageController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventMessageRepository eventMessageRepository;
    @Autowired
    private EventMessageResourceProcessor eventMessageResourceProcessor;

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {

        User currentUser = userRepository.findCurrentUser();

        FilterPayload filterPayload = params.getFilterPayload();
        PageRequest pageable = params.getPageable();

        Specification<EventMessage> specification = EventMessageSpecificationBuilder.buildSpecification(currentUser, filterPayload);
        Page<EventMessage> result = eventMessageRepository.findAll(specification, pageable);

        return ResponseEntity.ok(
                eventMessageResourceProcessor.toResourcePage(
                        result, params.getInclude(), eventMessageRepository.count(specification), pageable
                )
        );

    }

    @Data
    public static class FilterPayload {

        private List<MessageType> messageTypes;
        private List<Integer> fromIds;
        private List<Integer> toIds;
        private List<Integer> documentIds;

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

            if ( filter.containsKey("messageTypes") && filter.get("messageTypes").size() > 0 )
                for (String messageType : filter.get("messageTypes")) {
                    filterPayload.getMessageTypes().add( MessageType.valueOf(messageType) );
                }
            if ( filter.containsKey("fromIds") && filter.get("fromIds").size() > 0 )
                for (String fromId : filter.get("fromIds")) {
                    filterPayload.getFromIds().add( Integer.parseInt( fromId, 10 ) );
                }
            if ( filter.containsKey("toIds") && filter.get("toIds").size() > 0 )
                for (String toId : filter.get("toIds")) {
                    filterPayload.getToIds().add( Integer.parseInt( toId, 10 ) );
                }
            if ( filter.containsKey("documentIds") && filter.get("documentIds").size() > 0 )
                for (String documentId : filter.get("documentIds")) {
                    filterPayload.getDocumentIds().add( Integer.parseInt( documentId, 10 ) );
                }

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
