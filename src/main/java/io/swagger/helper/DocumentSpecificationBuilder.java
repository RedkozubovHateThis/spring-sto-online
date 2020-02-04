package io.swagger.helper;

import io.swagger.controller.DocumentDetailServiceController;
import io.swagger.firebird.model.*;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentSpecificationBuilder {

    public static Specification<DocumentServiceDetail> buildSpecificationList(User currentUser,
                                                                              DocumentDetailServiceController.FilterPayload filterPayload) {
        return buildSpecificationList( new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), currentUser, filterPayload );
    }

    public static Specification<DocumentServiceDetail> buildSpecificationList(List<Integer> clientIds, List<Integer> organizationIds, User currentUser,
                                                                              DocumentDetailServiceController.FilterPayload filterPayload) {
        return buildSpecificationList( clientIds, organizationIds, new ArrayList<>(), currentUser, filterPayload );
    }

    public static Specification<DocumentServiceDetail> buildSpecificationList(List<Integer> paidDocumentIds, User currentUser,
                                                                              DocumentDetailServiceController.FilterPayload filterPayload) {
        return buildSpecificationList( new ArrayList<>(), new ArrayList<>(), paidDocumentIds, currentUser, filterPayload );
    }

    public static Specification<DocumentServiceDetail> buildSpecificationList(List<Integer> clientIds, List<Integer> organizationIds, List<Integer> paidDocumentIds, User currentUser,
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
                Join<Model, Mark> markJoin = modelJoin.join(Model_.mark);

                if ( UserHelper.isModerator( currentUser ) ) {

                    if ( organizationIds.size() == 0 && clientIds.size() > 0 )
                        predicates.add( clientJoin.get( Client_.id ).in( clientIds ) );

                    else if ( organizationIds.size() > 0 && clientIds.size() == 0 )
                        predicates.add( orgJoin.get( Organization_.id ).in( organizationIds ) );

                    else if ( organizationIds.size() > 0 && clientIds.size() > 0 )
                        predicates.add(
                                cb.or(
                                        clientJoin.get( Client_.id ).in( clientIds ),
                                        orgJoin.get( Organization_.id ).in( organizationIds )
                                )
                        );

                }
                else if ( UserHelper.isClient( currentUser ) ) {
                    predicates.add( cb.equal( clientJoin.get( Client_.id ), currentUser.getClientId() ) );
                    predicates.add( cb.equal( dohJoin.get( DocumentOutHeader_.state ), (short) 4 ) );
                }
                else if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) ) {
                    predicates.add( cb.equal( orgJoin.get( Organization_.id ), currentUser.getOrganizationId() ) );

                    if ( UserHelper.isFreelancer( currentUser ) ) {
                        Join<DocumentOutHeader, Manager> managerJoin = dohJoin.join( DocumentOutHeader_.manager );
                        predicates.add( cb.equal( managerJoin.get( Manager_.id ), currentUser.getManagerId() ) );
                    }

                    if ( currentUser.getIsCurrentSubscriptionExpired() ) {
                        Subscription subscription = currentUser.getCurrentSubscription();

                        predicates.add(
                                cb.lessThanOrEqualTo(
                                        root.get( DocumentServiceDetail_.dateStart ), subscription.getEndDate()
                                )
                        );
                    }

                    if ( filterPayload.getPaymentState() != null ) {
                        switch ( filterPayload.getPaymentState() ) {
                            case PAID:
                                predicates.add( root.get( DocumentServiceDetail_.id ).in( paidDocumentIds ) );
                                break;
                            case NOT_PAID:
                                predicates.add( root.get( DocumentServiceDetail_.id ).in( paidDocumentIds ).not() );
                        }
                    }
                }

                if ( filterPayload.getStates() != null && filterPayload.getStates().size() > 0 ) {
                    predicates.add( dohJoin.get( DocumentOutHeader_.state ).in( filterPayload.getStates() ) );
                }
                if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) &&
                        filterPayload.getOrganizations() != null && filterPayload.getOrganizations().size() > 0 ) {
                    predicates.add( orgJoin.get( Organization_.id ).in( filterPayload.getOrganizations() ) );
                }
                if ( !UserHelper.isClient( currentUser ) &&
                        filterPayload.getClients() != null && filterPayload.getClients().size() > 0 ) {
                    predicates.add( clientJoin.get( Client_.id ).in( filterPayload.getClients() ) );
                }
                if ( filterPayload.getVehicle() != null && filterPayload.getVehicle().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.upper( mdJoin.get( ModelDetail_.regNumber ) ), "%" + filterPayload.getVehicle().toUpperCase() + "%"
                            )
                    );
//                    predicates.add(
//                            cb.or(
//                                    cb.like(
//                                            cb.upper( modelJoin.get( Model_.name ) ), "%" + filterPayload.getVehicle().toUpperCase() + "%"
//                                    ),
//                                    cb.like(
//                                            cb.upper( markJoin.get( Mark_.name ) ), "%" + filterPayload.getVehicle().toUpperCase() + "%"
//                                    )
//                            )
//                    );
                }
                if ( filterPayload.getVinNumber() != null && filterPayload.getVinNumber().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.upper( mdJoin.get( ModelDetail_.vinNumber ) ), "%" + filterPayload.getVinNumber().toUpperCase() + "%"
                            )
                    );
                }
                if ( filterPayload.getFromDate() != null && filterPayload.getToDate() != null ) {
                    predicates.add(
                            cb.between(
                                    root.get( DocumentServiceDetail_.dateStart ),
                                    filterPayload.getFromDate(), filterPayload.getToDate()
                            )
                    );
                }
                else if ( filterPayload.getFromDate() == null && filterPayload.getToDate() != null ) {
                    predicates.add(
                            cb.lessThanOrEqualTo(
                                    root.get( DocumentServiceDetail_.dateStart ),
                                    filterPayload.getToDate()
                            )
                    );
                }
                else if ( filterPayload.getFromDate() != null && filterPayload.getToDate() == null ) {
                    predicates.add(
                            cb.greaterThanOrEqualTo(
                                    root.get( DocumentServiceDetail_.dateStart ),
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
