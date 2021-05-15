package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.repository.EventMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class EventMessageResourceRepository implements ResourceRepository<EventMessage, Long> {

    @Autowired
    private EventMessageRepository eventMessageRepository;

    @Override
    public Class<EventMessage> getResourceClass() {
        return EventMessage.class;
    }

    @Override
    public EventMessage findOne(Long aLong, QuerySpec querySpec) {
        return eventMessageRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<EventMessage> findAll(QuerySpec querySpec) {
        return querySpec.apply( eventMessageRepository.findAll() );
    }

    @Override
    public ResourceList<EventMessage> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( eventMessageRepository.findAllById( collection ) );
    }

    @Override
    public <S extends EventMessage> S save(S s) {
        return eventMessageRepository.save( s );
    }

    @Override
    public <S extends EventMessage> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
