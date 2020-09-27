package io.swagger.helper;

import io.swagger.controller.PaymentController;
import io.swagger.postgres.model.*;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.model.payment.PaymentRecord_;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.Profile_;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRecordSpecificationBuilder {

    public static Specification<PaymentRecord> buildSpecification(PaymentController.PaymentRecordsPayload filterPayload) {

        return new Specification<PaymentRecord>() {
            @Override
            public Predicate toPredicate(Root<PaymentRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if ( filterPayload.getUserId() != null ) {
                    Join<PaymentRecord, User> userJoin = root.join( PaymentRecord_.user );

                    predicates.add(
                            cb.equal(
                                    userJoin.get( User_.id ), filterPayload.getUserId()
                            )
                    );
                }

                if ( filterPayload.getFromDate() != null && filterPayload.getToDate() != null ) {
                    predicates.add(
                            cb.between(
                                    root.get( PaymentRecord_.createDate ),
                                    filterPayload.getFromDate(), filterPayload.getToDate()
                            )
                    );
                }
                else if ( filterPayload.getFromDate() == null && filterPayload.getToDate() != null ) {
                    predicates.add(
                            cb.lessThanOrEqualTo(
                                    root.get( PaymentRecord_.createDate ),
                                    filterPayload.getToDate()
                            )
                    );
                }
                else if ( filterPayload.getFromDate() != null && filterPayload.getToDate() == null ) {
                    predicates.add(
                            cb.greaterThanOrEqualTo(
                                    root.get( PaymentRecord_.createDate ),
                                    filterPayload.getFromDate()
                            )
                    );
                }
                if ( filterPayload.getPaymentType() != null ) {
                    predicates.add(
                            cb.equal(
                                    root.get( PaymentRecord_.paymentType ),
                                    filterPayload.getPaymentType()
                            )
                    );
                }
                if ( filterPayload.getPaymentState() != null ) {
                    predicates.add(
                            cb.equal(
                                    root.get( PaymentRecord_.paymentState ),
                                    filterPayload.getPaymentState()
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
