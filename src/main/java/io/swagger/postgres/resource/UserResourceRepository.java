package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.MetaRepository;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.core.resource.meta.MetaInformation;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.resourceProcessor.JsonApiListMeta;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

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
        User currentUser = userRepository.findCurrentUser();

        User user = userRepository.findById(aLong).orElse( null );
        if ( user == null ) {
            throw new ResourceNotFoundException("Пользователь не найден!");
        }

        if ( currentUser.getId().equals( user.getId() ) ||
                UserHelper.isAdmin( currentUser ) )
            return user;

        throw new ForbiddenException("Пользователь недоступен!");
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
        if ( s.getUsername() == null || s.getUsername().isEmpty() )
            s.setUsername( UUID.randomUUID().toString() );

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
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять пользователей!");

        User user = userRepository.findById( aLong ).orElse(null);

        if ( user != null ) {

            if ( currentUser.getId().equals( user.getId() ) )
                throw new BadRequestException("Невозможно удалить активного пользователя!");

            user.setEnabled( false );
            userRepository.save(user);
        }
        else
            throw new ResourceNotFoundException("Пользователь не найден!");
    }

    @Override
    public MetaInformation getMetaInformation(Collection<User> resources, QuerySpec querySpec, MetaInformation current) {
        return new JsonApiListMeta(userRepository.count());
    }
}
