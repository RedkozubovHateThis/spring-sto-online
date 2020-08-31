package io.swagger.helper;

import io.swagger.controller.AdEntityController;
import io.swagger.controller.VehicleDictionaryController;
import io.swagger.postgres.model.AdEntity;
import io.swagger.postgres.model.AdEntity_;
import io.swagger.postgres.model.VehicleDictionary;
import io.swagger.postgres.model.VehicleDictionary_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AdEntitySpecificationBuilder {

    public static Specification<AdEntity> buildSpecification(AdEntityController.FilterPayload filterPayload) {

        return new Specification<AdEntity>() {
            @Override
            public Predicate toPredicate(Root<AdEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                
                if ( filterPayload.getName() != null && filterPayload.getName().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( AdEntity_.name ) ),
                                    ( "%" + filterPayload.getName() + "%" ).toLowerCase()
                            )
                    );
                }
                if ( filterPayload.getEmail() != null && filterPayload.getEmail().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( AdEntity_.email ) ),
                                    ( "%" + filterPayload.getEmail() + "%" ).toLowerCase()
                            )
                    );
                }
                if ( filterPayload.getPhone() != null && filterPayload.getPhone().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( AdEntity_.phone ) ),
                                    ( "%" + filterPayload.getPhone() + "%" ).toLowerCase()
                            )
                    );
                }
                if ( filterPayload.getActive() != null ) {
                    predicates.add(
                            cb.equal(
                                    root.get( AdEntity_.active ),
                                    filterPayload.getActive()
                            )
                    );
                }
                if ( filterPayload.getSideOffer() != null ) {
                    predicates.add(
                            cb.equal(
                                    root.get( AdEntity_.sideOffer ),
                                    filterPayload.getSideOffer()
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
