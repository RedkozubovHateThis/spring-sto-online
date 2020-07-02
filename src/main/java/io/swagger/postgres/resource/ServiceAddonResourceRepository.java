package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.ServiceAddon;
import io.swagger.postgres.repository.ServiceAddonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceAddonResourceRepository implements ResourceRepository<ServiceAddon, Long> {

    @Autowired
    private ServiceAddonRepository serviceAddonRepository;

    @Override
    public Class<ServiceAddon> getResourceClass() {
        return ServiceAddon.class;
    }

    @Override
    public ServiceAddon findOne(Long aLong, QuerySpec querySpec) {
        return serviceAddonRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<ServiceAddon> findAll(QuerySpec querySpec) {
        return querySpec.apply( serviceAddonRepository.findAll() );
    }

    @Override
    public ResourceList<ServiceAddon> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( serviceAddonRepository.findAllById( collection ) );
    }

    @Override
    public <S extends ServiceAddon> S save(S s) {
        return serviceAddonRepository.save( s );
    }

    @Override
    public <S extends ServiceAddon> S create(S s) {
        s.setDeleted(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
