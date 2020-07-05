package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ProfileResourceRepository implements ResourceRepository<Profile, Long> {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

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

        if ( s.getPhone() == null || s.getPhone().isEmpty() )
            throw new BadRequestException("Телефон не может быть пустым!");
        if ( !userService.isPhoneValid( s.getPhone() ) )
            throw new BadRequestException("Неверный номер телефона!");
        if ( s.getEmail() != null && s.getEmail().length() == 0 )
            s.setEmail(null);
        if ( s.getInn() != null && s.getInn().length() == 0 )
            s.setInn(null);

        userService.processPhone(s);

        Boolean isExistsByPhone;

        if ( s.getId() != null )
            isExistsByPhone = profileRepository.isProfileExistsPhoneNotSelf( s.getPhone(), s.getId() );
        else
            isExistsByPhone = profileRepository.isProfileExistsPhone( s.getPhone() );

        if ( isExistsByPhone )
            throw new BadRequestException("Данный телефон уже указан у другого профиля!");

        if ( s.getEmail() != null ) {

            Boolean isExistsByEmail;

            if ( s.getId() != null )
                isExistsByEmail = profileRepository.isProfileExistsEmailNotSelf( s.getEmail(), s.getId() );
            else
                isExistsByEmail = profileRepository.isProfileExistsEmail( s.getEmail() );

            if ( isExistsByEmail )
                throw new BadRequestException("Данная почта уже указана у другого профиля!");

        }

        if ( s.getInn() != null ) {

            Boolean isExistsByInn;

            if ( s.getId() != null )
                isExistsByInn = profileRepository.isProfileExistsInnNotSelf( s.getInn(), s.getId() );
            else
                isExistsByInn = profileRepository.isProfileExistsInn( s.getInn() );

            if ( isExistsByInn )
                throw new BadRequestException("Данный ИНН уже указан у другого профиля!");

        }

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
