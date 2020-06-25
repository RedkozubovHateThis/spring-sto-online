package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.ServiceWork;
import io.swagger.postgres.repository.ServiceWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceWorkResourceRepository implements ResourceRepository<ServiceWork, Long> {

    @Autowired
    private ServiceWorkRepository serviceWorkRepository;

    @Override
    public Class<ServiceWork> getResourceClass() {
        return ServiceWork.class;
    }

    @Override
    public ServiceWork findOne(Long aLong, QuerySpec querySpec) {
        return serviceWorkRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<ServiceWork> findAll(QuerySpec querySpec) {
        return querySpec.apply( serviceWorkRepository.findAll() );
    }

    @Override
    public ResourceList<ServiceWork> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( serviceWorkRepository.findAllById( collection ) );
    }

    @Override
    public <S extends ServiceWork> S save(S s) {
        return serviceWorkRepository.save( s );
    }

    @Override
    public <S extends ServiceWork> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
