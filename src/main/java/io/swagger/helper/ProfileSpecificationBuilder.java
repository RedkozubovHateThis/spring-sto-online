package io.swagger.helper;

import io.swagger.controller.ProfileController;
import io.swagger.controller.ServiceDocumentController;
import io.swagger.postgres.model.*;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.Profile_;
import io.swagger.postgres.model.security.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileSpecificationBuilder {

    public static Specification<Profile> buildSpecification(User currentUser, ProfileController.FilterPayload filterPayload) {

        return new Specification<Profile>() {
            @Override
            public Predicate toPredicate(Root<Profile> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                Join<Profile, User> userJoin = root.join( Profile_.user, JoinType.LEFT );

                predicates.add(
                        cb.isNull(
                                userJoin
                        )
                );

                if ( filterPayload.getFio() != null && filterPayload.getFio().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Profile_.name ) ),
                                    ( "%" + filterPayload.getFio() + "%" ).toLowerCase()
                            )
                    );
                }

                if ( filterPayload.getInn() != null && filterPayload.getInn().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Profile_.inn ) ),
                                    ( "%" + filterPayload.getInn() + "%" ).toLowerCase()
                            )
                    );
                }

                if ( filterPayload.getEmail() != null && filterPayload.getEmail().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Profile_.email ) ),
                                    ( "%" + filterPayload.getEmail() + "%" ).toLowerCase()
                            )
                    );
                }

                if ( filterPayload.getPhone() != null && filterPayload.getPhone().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Profile_.phone ) ),
                                    ( "%" + filterPayload.getPhone() + "%" ).toLowerCase()
                            )
                    );
                }

                if ( filterPayload.getAddress() != null && filterPayload.getAddress().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Profile_.address ) ),
                                    ( "%" + filterPayload.getAddress() + "%" ).toLowerCase()
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
