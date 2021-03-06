package io.swagger.helper;

import io.swagger.controller.ServiceDocumentController;
import io.swagger.postgres.model.*;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.Profile_;
import io.swagger.postgres.model.security.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDocumentSpecificationBuilder {

    public static Specification<ServiceDocument> buildSpecification(User currentUser, ServiceDocumentController.FilterPayload filterPayload) {

        return new Specification<ServiceDocument>() {
            @Override
            public Predicate toPredicate(Root<ServiceDocument> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                Join<ServiceDocument, Vehicle> vehicleJoin = root.join( ServiceDocument_.vehicle, JoinType.LEFT );
                Join<ServiceDocument, Profile> clientJoin = root.join( ServiceDocument_.client, JoinType.LEFT );
                Join<ServiceDocument, Profile> executorJoin = root.join( ServiceDocument_.executor, JoinType.LEFT );

                if ( UserHelper.isClient( currentUser ) ) {
                    predicates.add( cb.equal(
                            clientJoin.get( Profile_.id ), currentUser.getProfile().getId()
                    ) );
                }
                else if ( filterPayload.getClient() != null ) {
                    predicates.add( cb.equal(
                            clientJoin.get( Profile_.id ), filterPayload.getClient()
                    ) );
                }
                if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) ) {
                    predicates.add( cb.equal(
                            executorJoin.get( Profile_.id ), currentUser.getProfile().getId()
                    ) );
                }
                else if ( filterPayload.getOrganization() != null ) {
                    predicates.add( cb.equal(
                            executorJoin.get( Profile_.id ), filterPayload.getOrganization()
                    ) );
                }

                if ( filterPayload.getState() != null ) {
                    predicates.add( root.get( ServiceDocument_.status ).in( filterPayload.getState() ) );
                }
                if ( filterPayload.getPaidState() != null ) {
                    predicates.add( root.get( ServiceDocument_.paidStatus ).in( filterPayload.getPaidState() ) );
                }
                if ( filterPayload.getVehicle() != null && filterPayload.getVehicle().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.upper( vehicleJoin.get( Vehicle_.regNumber ) ), "%" + filterPayload.getVehicle().toUpperCase() + "%"
                            )
                    );
                }
                if ( filterPayload.getVinNumber() != null && filterPayload.getVinNumber().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.upper( vehicleJoin.get( Vehicle_.vinNumber ) ), "%" + filterPayload.getVinNumber().toUpperCase() + "%"
                            )
                    );
                }
                if ( filterPayload.getFromDate() != null && filterPayload.getToDate() != null ) {
                    predicates.add(
                            cb.between(
                                    root.get( ServiceDocument_.startDate ),
                                    filterPayload.getFromDate(), filterPayload.getToDate()
                            )
                    );
                }
                if ( !UserHelper.isClient( currentUser ) && filterPayload.getCustomer() != null ) {
                    Join<ServiceDocument, Customer> customerJoin = root.join( ServiceDocument_.customer );
                    predicates.add(
                            cb.equal(
                                    customerJoin.get( Customer_.id ), filterPayload.getCustomer()
                            )
                    );
                }
                else if ( filterPayload.getFromDate() == null && filterPayload.getToDate() != null ) {
                    predicates.add(
                            cb.lessThanOrEqualTo(
                                    root.get( ServiceDocument_.startDate ),
                                    filterPayload.getToDate()
                            )
                    );
                }
                else if ( filterPayload.getFromDate() != null && filterPayload.getToDate() == null ) {
                    predicates.add(
                            cb.greaterThanOrEqualTo(
                                    root.get( ServiceDocument_.startDate ),
                                    filterPayload.getFromDate()
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
