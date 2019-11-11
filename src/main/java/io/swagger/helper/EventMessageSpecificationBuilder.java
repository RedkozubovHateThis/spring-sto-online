package io.swagger.helper;

import io.swagger.controller.DocumentDetailServiceController;
import io.swagger.controller.EventMessageController;
import io.swagger.firebird.model.*;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.EventMessage_;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.User_;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class EventMessageSpecificationBuilder {

    public static Specification<EventMessage> buildSpecification(User currentUser, EventMessageController.FilterPayload filterPayload) {

        return new Specification<EventMessage>() {
            @Override
            public Predicate toPredicate(Root<EventMessage> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                Join<EventMessage, User> fromUserJoin = root.join( EventMessage_.sendUser );
                Join<EventMessage, User> toUserJoin = root.join( EventMessage_.targetUser );

                predicates.add(
                        cb.equal( fromUserJoin.get( User_.enabled ), true )
                );
                predicates.add(
                        cb.equal( toUserJoin.get( User_.enabled ), true )
                );

                if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
                    predicates.add( cb.equal( root.get( EventMessage_.messageType ), MessageType.DOCUMENT_CHANGE ) );
                }
                else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
                    predicates.add(
                            cb.equal( toUserJoin.get( User_.id ), currentUser.getId() )
                    );
                }
                else if ( UserHelper.hasRole( currentUser, "CLIENT" ) ||
                        UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) ) {
                    predicates.add(
                        cb.or(
                                cb.equal( root.get( EventMessage_.messageType ), MessageType.USER_APPROVE ),
                                cb.equal( root.get( EventMessage_.messageType ), MessageType.USER_REJECT )
                        )
                    );
                    predicates.add(
                            cb.equal( toUserJoin.get( User_.id ), currentUser.getId() )
                    );
                }

                if ( filterPayload.getMessageTypes() != null && filterPayload.getMessageTypes().size() > 0 ) {
                    predicates.add( root.get( EventMessage_.messageType ).in( filterPayload.getMessageTypes() ) );
                }
                if ( filterPayload.getFromIds() != null && filterPayload.getFromIds().size() > 0 ) {
                    predicates.add( fromUserJoin.get( User_.id ).in( filterPayload.getFromIds() ) );
                }
                if ( filterPayload.getToIds() != null && filterPayload.getToIds().size() > 0 ) {
                    predicates.add( toUserJoin.get( User_.id ).in( filterPayload.getToIds() ) );
                }
                if ( filterPayload.getDocumentIds() != null && filterPayload.getDocumentIds().size() > 0 ) {
                    predicates.add( root.get( EventMessage_.documentId ).in( filterPayload.getDocumentIds() ) );
                }

                query.distinct(true);

                query.where(predicates.toArray(new Predicate[0]));
                return null;
            }
        };

    }

}
