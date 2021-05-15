package io.swagger.helper;

import io.swagger.controller.ServiceWorkDictionaryController;
import io.swagger.controller.ServiceWorkDictionaryController;
import io.swagger.postgres.model.*;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.Profile_;
import io.swagger.postgres.model.security.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceWorkDictionarySpecificationBuilder {

    public static Specification<ServiceWorkDictionary> buildSpecification(ServiceWorkDictionaryController.FilterPayload filterPayload) {

        return new Specification<ServiceWorkDictionary>() {
            @Override
            public Predicate toPredicate(Root<ServiceWorkDictionary> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                
                if ( filterPayload.getName() != null && filterPayload.getName().length() > 0 ) {
                    predicates.add(
                            cb.like(
                                    cb.lower( root.get( ServiceWorkDictionary_.name ) ),
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
