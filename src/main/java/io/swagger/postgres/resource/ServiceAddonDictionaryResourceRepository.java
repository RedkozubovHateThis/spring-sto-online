package io.swagger.postgres.resource;

import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceAddonDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ServiceAddonDictionaryRepository;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ServiceAddonDictionaryResourceRepository implements ResourceRepository<ServiceAddonDictionary, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceAddonDictionaryRepository serviceAddonDictionaryRepository;

    @Override
    public Class<ServiceAddonDictionary> getResourceClass() {
        return ServiceAddonDictionary.class;
    }

    @Override
    public ServiceAddonDictionary findOne(Long aLong, QuerySpec querySpec) {
        return serviceAddonDictionaryRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<ServiceAddonDictionary> findAll(QuerySpec querySpec) {
        return querySpec.apply( serviceAddonDictionaryRepository.findAll() );
    }

    @Override
    public ResourceList<ServiceAddonDictionary> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( serviceAddonDictionaryRepository.findAllById( collection ) );
    }

    @Override
    public <S extends ServiceAddonDictionary> S save(S s) {
        return serviceAddonDictionaryRepository.save( s );
    }

    @Override
    public <S extends ServiceAddonDictionary> S create(S s) {
        s.setDeleted(false);
        s.setIsInitial(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять справочники товаров!");

        ServiceAddonDictionary serviceAddonDictionary = serviceAddonDictionaryRepository.findById(aLong).orElse(null);

        if ( serviceAddonDictionary == null )
            throw new ResourceNotFoundException("Справочник товаров не найден!");

        serviceAddonDictionary.setDeleted(true);
        serviceAddonDictionaryRepository.save(serviceAddonDictionary);
    }
}
