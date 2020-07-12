package io.swagger.helper;

import io.swagger.controller.VehicleDictionaryController;
import io.swagger.postgres.model.VehicleDictionary;
import io.swagger.postgres.model.VehicleDictionary_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class VehicleDictionarySpecificationBuilder {

    public static Specification<VehicleDictionary> buildSpecification(VehicleDictionaryController.FilterPayload filterPayload) {

        return new Specification<VehicleDictionary>() {
            @Override
            public Predicate toPredicate(Root<VehicleDictionary> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                
                if ( filterPayload.getName() != null && filterPayload.getName().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( VehicleDictionary_.name ) ),
                                    ( "%" + filterPayload.getName() + "%" ).toLowerCase()
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
