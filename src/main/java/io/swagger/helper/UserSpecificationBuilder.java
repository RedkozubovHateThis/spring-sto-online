package io.swagger.helper;

import io.swagger.controller.UserController;
import io.swagger.postgres.model.security.*;
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
                Join<User, Profile> profileJoin = root.join( User_.profile, JoinType.INNER );

                predicates.add( cb.equal( profileJoin.get( Profile_.deleted ), false ) );

                if ( filterPayload.getRole() != null && filterPayload.getRole().length() > 0 ) {
                    predicates.add(
                            cb.equal( userRoleJoin.get( UserRole_.name ), filterPayload.getRole() )
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
                            cb.like( profileJoin.get( Profile_.email ), "%" + filterPayload.getEmail() + "%" )
                    );
                }
                if ( filterPayload.getPhone() != null && filterPayload.getPhone().length() > 0 ) {
                    predicates.add(
                            cb.like( profileJoin.get( Profile_.phone ), "%" + filterPayload.getPhone() + "%" )
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
                if ( filterPayload.getInn() != null && filterPayload.getInn().length() > 0 ) {
                    predicates.add(
                            cb.like( profileJoin.get( Profile_.inn ), "%" + filterPayload.getInn() + "%" )
                    );
                }

                query.distinct(true);

                query.where(predicates.toArray(new Predicate[0]));
                return null;
            }
        };

    }

}
