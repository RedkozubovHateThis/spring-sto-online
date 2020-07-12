package io.swagger.helper;

import io.swagger.controller.CustomerController;
import io.swagger.postgres.model.Customer;
import io.swagger.postgres.model.Customer_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerSpecificationBuilder {

    public static Specification<Customer> buildSpecification(CustomerController.FilterPayload filterPayload) {

        return new Specification<Customer>() {
            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if ( filterPayload.getEmail() != null && filterPayload.getEmail().length() > 0 ) {
                    predicates.add(
                            cb.like( root.get( Customer_.email ), "%" + filterPayload.getEmail() + "%" )
                    );
                }
                if ( filterPayload.getPhone() != null && filterPayload.getPhone().length() > 0 ) {
                    predicates.add(
                            cb.like( root.get( Customer_.phone ), "%" + filterPayload.getPhone() + "%" )
                    );
                }
                if ( filterPayload.getInn() != null && filterPayload.getInn().length() > 0 ) {
                    predicates.add(
                            cb.like( root.get( Customer_.inn ), "%" + filterPayload.getInn() + "%" )
                    );
                }
                if ( filterPayload.getEmail() != null && filterPayload.getEmail().length() > 0 ) {
                    predicates.add(
                            cb.like( root.get( Customer_.email ), "%" + filterPayload.getEmail() + "%" )
                    );
                }

                query.distinct(true);

                query.where(predicates.toArray(new Predicate[0]));
                return null;
            }
        };

    }

}
