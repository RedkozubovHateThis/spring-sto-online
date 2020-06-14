package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.repository.SubscriptionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SubscriptionTypeResourceRepository implements ResourceRepository<SubscriptionType, Long> {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Override
    public Class<SubscriptionType> getResourceClass() {
        return SubscriptionType.class;
    }

    @Override
    public SubscriptionType findOne(Long aLong, QuerySpec querySpec) {
        return subscriptionTypeRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<SubscriptionType> findAll(QuerySpec querySpec) {
        return querySpec.apply( subscriptionTypeRepository.findAll() );
    }

    @Override
    public ResourceList<SubscriptionType> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( subscriptionTypeRepository.findAllById( collection ) );
    }

    @Override
    public <S extends SubscriptionType> S save(S s) {
        return subscriptionTypeRepository.save( s );
    }

    @Override
    public <S extends SubscriptionType> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
