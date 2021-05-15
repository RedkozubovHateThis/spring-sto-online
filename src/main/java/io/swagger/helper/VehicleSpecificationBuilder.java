package io.swagger.helper;

import io.swagger.controller.VehicleController;
import io.swagger.controller.VehicleController;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.model.Vehicle_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class VehicleSpecificationBuilder {

    public static Specification<Vehicle> buildSpecification(VehicleController.FilterPayload filterPayload) {

        return new Specification<Vehicle>() {
            @Override
            public Predicate toPredicate(Root<Vehicle> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                
                if ( filterPayload.getModelName() != null && filterPayload.getModelName().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Vehicle_.modelName ) ),
                                    ( "%" + filterPayload.getModelName() + "%" ).toLowerCase()
                            )
                    );
                }
                if ( filterPayload.getRegNumber() != null && filterPayload.getRegNumber().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Vehicle_.regNumber ) ),
                                    ( "%" + filterPayload.getRegNumber() + "%" ).toLowerCase()
                            )
                    );
                }
                if ( filterPayload.getVinNumber() != null && filterPayload.getVinNumber().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( Vehicle_.vinNumber ) ),
                                    ( "%" + filterPayload.getVinNumber() + "%" ).toLowerCase()
                            )
                    );
                }
                if ( filterPayload.getYear() != null ) {
                    predicates.add(
                            cb.equal(
                                    root.get( Vehicle_.year ),
                                    filterPayload.getYear()
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
