package io.swagger.postgres.resource;

import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.VehicleDictionary;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.VehicleDictionaryRepository;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class VehicleDictionaryResourceRepository implements ResourceRepository<VehicleDictionary, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleDictionaryRepository vehicleDictionaryRepository;

    @Override
    public Class<VehicleDictionary> getResourceClass() {
        return VehicleDictionary.class;
    }

    @Override
    public VehicleDictionary findOne(Long aLong, QuerySpec querySpec) {
        return vehicleDictionaryRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<VehicleDictionary> findAll(QuerySpec querySpec) {
        return querySpec.apply( vehicleDictionaryRepository.findAll() );
    }

    @Override
    public ResourceList<VehicleDictionary> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( vehicleDictionaryRepository.findAllById( collection ) );
    }

    @Override
    public <S extends VehicleDictionary> S save(S s) {
        return vehicleDictionaryRepository.save( s );
    }

    @Override
    public <S extends VehicleDictionary> S create(S s) {
        s.setDeleted(false);
        s.setIsInitial(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять справочники автомобилей!");

        VehicleDictionary vehicleDictionary = vehicleDictionaryRepository.findById(aLong).orElse(null);

        if ( vehicleDictionary == null )
            throw new ResourceNotFoundException("Справочник автомобилей не найден!");

        vehicleDictionary.setDeleted(true);
        vehicleDictionaryRepository.save(vehicleDictionary);
    }
}
