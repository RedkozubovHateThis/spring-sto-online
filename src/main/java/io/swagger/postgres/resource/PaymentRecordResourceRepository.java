package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.payment.PaymentRecord;
import io.swagger.postgres.repository.PaymentRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PaymentRecordResourceRepository implements ResourceRepository<PaymentRecord, Long> {

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Override
    public Class<PaymentRecord> getResourceClass() {
        return PaymentRecord.class;
    }

    @Override
    public PaymentRecord findOne(Long aLong, QuerySpec querySpec) {
        return paymentRecordRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<PaymentRecord> findAll(QuerySpec querySpec) {
        return querySpec.apply( paymentRecordRepository.findAll() );
    }

    @Override
    public ResourceList<PaymentRecord> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( paymentRecordRepository.findAllById( collection ) );
    }

    @Override
    public <S extends PaymentRecord> S save(S s) {
        return paymentRecordRepository.save( s );
    }

    @Override
    public <S extends PaymentRecord> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {

    }
}
