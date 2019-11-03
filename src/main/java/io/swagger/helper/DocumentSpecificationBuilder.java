package io.swagger.helper;

import io.swagger.controller.DocumentDetailServiceController;
import io.swagger.firebird.model.*;
import io.swagger.postgres.model.security.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentSpecificationBuilder {

    public static Specification<DocumentServiceDetail> buildSpecificationList(User currentUser,
                                                                              DocumentDetailServiceController.FilterPayload filterPayload) {
        return buildSpecificationList( new ArrayList<>(), new ArrayList<>(), currentUser, filterPayload );
    }

    public static Specification<DocumentServiceDetail> buildSpecificationList(List<Integer> clientIds, List<Integer> organizationIds, User currentUser,
                                                                              DocumentDetailServiceController.FilterPayload filterPayload) {

        return new Specification<DocumentServiceDetail>() {
            @Override
            public Predicate toPredicate(Root<DocumentServiceDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                Join<DocumentServiceDetail, DocumentOutHeader> dohJoin = root.join( DocumentServiceDetail_.documentOutHeader );
                Join<DocumentOutHeader, DocumentOut> doJoin = dohJoin.join( DocumentOutHeader_.documentOut );
                Join<DocumentOut, Client> clientJoin = doJoin.join( DocumentOut_.client );
                Join<DocumentOut, Organization> orgJoin = doJoin.join(DocumentOut_.organization);
                Join<DocumentServiceDetail, ModelLink> mlJoin = root.join(DocumentServiceDetail_.modelLink);
                Join<ModelLink, ModelDetail> mdJoin = mlJoin.join(ModelLink_.modelDetail);
                Join<ModelDetail, Model> modelJoin = mdJoin.join(ModelDetail_.model);

                if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {

                    if ( organizationIds.size() == 0 && clientIds.size() > 0 )
                        predicates.add( clientJoin.get( Client_.id ).in( clientIds ) );

                    else if ( organizationIds.size() > 0 && clientIds.size() == 0 )
                        predicates.add( orgJoin.get( Organization_.id ).in( organizationIds ) );

                    else if ( organizationIds.size() > 0 && clientIds.size() > 0 )
                        predicates.add( cb.or( clientJoin.get( Client_.id ).in( clientIds ), orgJoin.get( Organization_.id ).in( organizationIds ) ) );

                }
                else if ( UserHelper.hasRole( currentUser, "CLIENT" ) ) {
                    predicates.add( cb.equal( clientJoin.get( Client_.id ), currentUser.getClientId() ) );
                }
                else if ( UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) ) {
                    predicates.add( cb.equal( orgJoin.get( Organization_.id ), currentUser.getOrganizationId() ) );
                }

                if ( filterPayload.getStates() != null && filterPayload.getStates().size() > 0 ) {
                    predicates.add( dohJoin.get( DocumentOutHeader_.state ).in( filterPayload.getStates() ) );
                }
                if ( filterPayload.getOrganizations() != null && filterPayload.getOrganizations().size() > 0 ) {
                    predicates.add( orgJoin.get( Organization_.id ).in( filterPayload.getOrganizations() ) );
                }
                if ( filterPayload.getVehicles() != null && filterPayload.getVehicles().size() > 0 ) {
                    predicates.add( modelJoin.get( Model_.id ).in( filterPayload.getVehicles() ) );
                }

                query.distinct(true);

                query.where(predicates.toArray(new Predicate[0]));
                return null;
            }
        };

    }

}
