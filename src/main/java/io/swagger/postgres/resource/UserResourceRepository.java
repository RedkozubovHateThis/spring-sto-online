package io.swagger.postgres.resource;

import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.MetaRepository;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.core.resource.meta.MetaInformation;
import io.swagger.postgres.resourceProcessor.JsonApiListMeta;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UserResourceRepository implements ResourceRepository<User, Long>, MetaRepository<User> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Override
    public Class<User> getResourceClass() {
        return User.class;
    }

    @Override
    public User findOne(Long aLong, QuerySpec querySpec) {
        return userRepository.findById( aLong ).orElse(null);
    }

    @Override
    public ResourceList<User> findAll(QuerySpec querySpec) {
        return querySpec.apply( userRepository.findAll() );
    }

    @Override
    public ResourceList<User> findAll(Collection<Long> collection, QuerySpec querySpec) {
        return querySpec.apply( userRepository.findAllById( collection ) );
    }

    @Override
    public <S extends User> S save(S s) {
        return userRepository.save( s );
    }

    @Override
    public <S extends User> S create(S s) {
        s.setEnabled(true);
        s.setIsAutoRegistered(false);
        s.setPassword( userPasswordEncoder.encode( s.getRawPassword() ) );
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User user = userRepository.findById( aLong ).orElse(null);

        if ( user != null ) {
            user.setEnabled( false );
            userRepository.save(user);
        }
    }

    @Override
    public MetaInformation getMetaInformation(Collection<User> resources, QuerySpec querySpec, MetaInformation current) {
        return new JsonApiListMeta(userRepository.count());
    }
}
