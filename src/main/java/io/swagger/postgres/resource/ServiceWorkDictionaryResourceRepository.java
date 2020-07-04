package io.swagger.postgres.resource;

import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceWorkDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceWorkDictionaryRepository;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceWorkDictionaryResourceRepository implements ResourceRepository<ServiceWorkDictionary, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceWorkDictionaryRepository serviceWorkDictionaryRepository;

    @Override
    public Class<ServiceWorkDictionary> getResourceClass() {
        return ServiceWorkDictionary.class;
    }

    @Override
    public ServiceWorkDictionary findOne(Long aLong, QuerySpec querySpec) {
        return serviceWorkDictionaryRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<ServiceWorkDictionary> findAll(QuerySpec querySpec) {
        return querySpec.apply( serviceWorkDictionaryRepository.findAll() );
    }

    @Override
    public ResourceList<ServiceWorkDictionary> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( serviceWorkDictionaryRepository.findAllById( collection ) );
    }

    @Override
    public <S extends ServiceWorkDictionary> S save(S s) {
        return serviceWorkDictionaryRepository.save( s );
    }

    @Override
    public <S extends ServiceWorkDictionary> S create(S s) {
        s.setDeleted(false);
        s.setIsInitial(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isServiceLeader( currentUser ) && !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять справочники работ!");

        ServiceWorkDictionary serviceWorkDictionary = serviceWorkDictionaryRepository.findById(aLong).orElse(null);

        if ( serviceWorkDictionary == null )
            throw new ResourceNotFoundException("Справочник работ не найден!");

        serviceWorkDictionary.setDeleted(true);
        serviceWorkDictionaryRepository.save(serviceWorkDictionary);
    }
}
