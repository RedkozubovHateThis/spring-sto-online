package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SubscriptionResourceRepository implements ResourceRepository<Subscription, Long> {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Class<Subscription> getResourceClass() {
        return Subscription.class;
    }

    @Override
    public Subscription findOne(Long aLong, QuerySpec querySpec) {
        return subscriptionRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<Subscription> findAll(QuerySpec querySpec) {
        return querySpec.apply( subscriptionRepository.findAll() );
    }

    @Override
    public ResourceList<Subscription> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( subscriptionRepository.findAllById( collection ) );
    }

    @Override
    public <S extends Subscription> S save(S s) {
        return subscriptionRepository.save( s );
    }

    @Override
    public <S extends Subscription> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
