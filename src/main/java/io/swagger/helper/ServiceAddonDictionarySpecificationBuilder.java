package io.swagger.helper;

import io.swagger.controller.ServiceAddonDictionaryController;
import io.swagger.postgres.model.ServiceAddonDictionary;
import io.swagger.postgres.model.ServiceAddonDictionary_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ServiceAddonDictionarySpecificationBuilder {

    public static Specification<ServiceAddonDictionary> buildSpecification(ServiceAddonDictionaryController.FilterPayload filterPayload) {

        return new Specification<ServiceAddonDictionary>() {
            @Override
            public Predicate toPredicate(Root<ServiceAddonDictionary> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                
                if ( filterPayload.getName() != null && filterPayload.getName().length() > 0 ) {
                    predicates.add(
                            cb.equal(
                                    cb.lower( root.get( ServiceAddonDictionary_.name ) ),
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
