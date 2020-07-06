package io.swagger.controller;

import io.swagger.helper.EventMessageSpecificationBuilder;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.resourceProcessor.EventMessageResourceProcessor;
import io.swagger.response.EventMessageResponse;
import io.swagger.response.api.JsonApiParamsBase;
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
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("messageTypes") && getFilter().get("messageTypes").size() > 0 )
                for (String messageType : getFilter().get("messageTypes")) {
                    filterPayload.getMessageTypes().add( MessageType.valueOf(messageType) );
                }
            if ( getFilter().containsKey("fromIds") && getFilter().get("fromIds").size() > 0 )
                for (String fromId : getFilter().get("fromIds")) {
                    filterPayload.getFromIds().add( Integer.parseInt( fromId, 10 ) );
                }
            if ( getFilter().containsKey("toIds") && getFilter().get("toIds").size() > 0 )
                for (String toId : getFilter().get("toIds")) {
                    filterPayload.getToIds().add( Integer.parseInt( toId, 10 ) );
                }
            if ( getFilter().containsKey("documentIds") && getFilter().get("documentIds").size() > 0 )
                for (String documentId : getFilter().get("documentIds")) {
                    filterPayload.getDocumentIds().add( Integer.parseInt( documentId, 10 ) );
                }

            return filterPayload;
        }
    }

}
