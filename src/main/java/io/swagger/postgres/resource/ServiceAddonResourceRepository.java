package io.swagger.postgres.resource;

import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceAddon;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceAddonRepository;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceAddonResourceRepository implements ResourceRepository<ServiceAddon, Long> {

    @Autowired
    private UserRepository userRepository;

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
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять товары!");

        ServiceAddon serviceAddon = serviceAddonRepository.findById(aLong).orElse(null);

        if ( serviceAddon == null )
            throw new ResourceNotFoundException("Товар не найден!");

        serviceAddon.setDeleted(true);
        serviceAddonRepository.save(serviceAddon);
    }
}
