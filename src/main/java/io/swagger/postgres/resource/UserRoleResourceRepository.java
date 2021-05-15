package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UserRoleResourceRepository implements ResourceRepository<UserRole, Long> {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public Class<UserRole> getResourceClass() {
        return UserRole.class;
    }

    @Override
    public UserRole findOne(Long aLong, QuerySpec querySpec) {
        return userRoleRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<UserRole> findAll(QuerySpec querySpec) {
        return querySpec.apply( userRoleRepository.findAll() );
    }

    @Override
    public ResourceList<UserRole> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( userRoleRepository.findAllById( collection ) );
    }

    @Override
    public <S extends UserRole> S save(S s) {
        return userRoleRepository.save( s );
    }

    @Override
    public <S extends UserRole> S create(S s) {
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
