package io.swagger.postgres.resource;

import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceWork;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceWorkRepository;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceWorkResourceRepository implements ResourceRepository<ServiceWork, Long> {

    @Autowired
    private UserRepository userRepository;

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
        s.setDeleted(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isServiceLeader( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять работы!");

        ServiceWork serviceWork = serviceWorkRepository.findById(aLong).orElse(null);

        if ( serviceWork == null )
            throw new ResourceNotFoundException("Работа не найдена!");

        serviceWork.setDeleted(true);
        serviceWorkRepository.save(serviceWork);
    }
}
