package io.swagger.helper;

import io.swagger.controller.EventMessageController;
import io.swagger.controller.UserController;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.EventMessage_;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.model.security.UserRole_;
import io.swagger.postgres.model.security.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class UserSpecificationBuilder {

    public static Specification<User> buildSpecification(User currentUser, UserController.FilterPayload filterPayload) {

        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                Join<User, UserRole> userRoleJoin = root.join( User_.roles );

                if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
                    predicates.add(
                            cb.or(
                                cb.equal( root.get( User_.moderatorId ), currentUser.getId() ),
                                cb.equal( root.get( User_.id ), currentUser.getId() )
                            )
                    );
                }

                if ( filterPayload.getRole() != null && filterPayload.getRole().length() > 0 ) {
                    predicates.add(
                            cb.equal( userRoleJoin.get( UserRole_.name ), filterPayload.getRole() )
                    );
                }
                if ( filterPayload.getIsApproved() != null ) {
                    predicates.add(
                            cb.equal( root.get( User_.isApproved ), filterPayload.getIsApproved() )
                    );
                    predicates.add(
                            cb.or(
                                    cb.equal( userRoleJoin.get( UserRole_.name ), "CLIENT" ),
                                    cb.equal( userRoleJoin.get( UserRole_.name ), "SERVICE_LEADER" )
                            )
                    );
                }
                if ( filterPayload.getIsAutoRegistered() != null ) {
                    predicates.add(
                            cb.equal( root.get( User_.isAutoRegistered ), filterPayload.getIsAutoRegistered() )
                    );
                    predicates.add(
                            cb.or(
                                    cb.equal( userRoleJoin.get( UserRole_.name ), "CLIENT" ),
                                    cb.equal( userRoleJoin.get( UserRole_.name ), "SERVICE_LEADER" )
                            )
                    );
                }
                if ( filterPayload.getEmail() != null && filterPayload.getEmail().length() > 0 ) {
                    predicates.add(
                            cb.like( root.get( User_.email ), "%" + filterPayload.getEmail() + "%" )
                    );
                }
                if ( filterPayload.getPhone() != null && filterPayload.getPhone().length() > 0 ) {
                    predicates.add(
                            cb.like( root.get( User_.phone ), "%" + filterPayload.getPhone() + "%" )
                    );
                }
                if ( filterPayload.getFio() != null && filterPayload.getFio().length() > 0 ) {
                    predicates.add(
                            cb.or(
                                    cb.like( root.get( User_.firstName ), "%" + filterPayload.getFio() + "%" ),
                                    cb.like( root.get( User_.lastName ), "%" + filterPayload.getFio() + "%" ),
                                    cb.like( root.get( User_.middleName ), "%" + filterPayload.getFio() + "%" )
                            )
                    );
                }

                query.distinct(true);

                query.where(predicates.toArray(new Predicate[0]));
                return null;
            }
        };

    }

}
