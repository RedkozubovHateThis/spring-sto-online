package io.swagger.controller;

import io.swagger.helper.EventMessageSpecificationBuilder;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.EventMessageResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/eventMessages")
@RestController
public class EventMessageController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventMessageRepository eventMessageRepository;

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable, FilterPayload filterPayload) {

        User currentUser = userRepository.findCurrentUser();

        Specification<EventMessage> specification = EventMessageSpecificationBuilder.buildSpecification(currentUser, filterPayload);
        Page<EventMessage> result = eventMessageRepository.findAll(specification, pageable);

        List<EventMessage> resultList = result.getContent();
        List<EventMessageResponse> responseList = resultList.stream()
                .map(EventMessageResponse::new).collect( Collectors.toList() );

        Page<EventMessageResponse> responsePage = new PageImpl<>(responseList, pageable, result.getTotalElements());

        return ResponseEntity.ok( responsePage );

    }

    @Data
    public static class FilterPayload {

        private List<MessageType> messageTypes;
        private List<Integer> fromIds;
        private List<Integer> toIds;
        private List<Integer> documentIds;

    }

}
