package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ProfileResourceRepository implements ResourceRepository<Profile, Long> {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Class<Profile> getResourceClass() {
        return Profile.class;
    }

    @Override
    public Profile findOne(Long aLong, QuerySpec querySpec) {
        return profileRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<Profile> findAll(QuerySpec querySpec) {
        return querySpec.apply( profileRepository.findAll() );
    }

    @Override
    public ResourceList<Profile> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( profileRepository.findAllById( collection ) );
    }

    @Override
    public <S extends Profile> S save(S s) {
        return profileRepository.save( s );
    }

    @Override
    public <S extends Profile> S create(S s) {
        s.setDeleted(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
    }
}
