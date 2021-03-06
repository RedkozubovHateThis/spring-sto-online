package io.swagger.postgres.resource;

import io.crnk.core.exception.BadRequestException;
import io.crnk.core.exception.ForbiddenException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ServiceDocument;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfileResourceRepository implements ResourceRepository<Profile, Long> {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;

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

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null && s.getId() == null ) {
            s.setCreatedBy( currentUser.getProfile() );
        }

        boolean isNewByFio = s.getId() == null && s.getByFio() != null && s.getByFio() &&
                ( s.getName() == null || s.getName().length() == 0 );
        boolean isNotNewByFio = s.getId() != null && s.getByFio() != null && s.getByFio();
        if ( isNewByFio || isNotNewByFio ) {
            s.setName( String.format( "%s %s %s", s.getLastName(), s.getFirstName(), s.getMiddleName() ) );
        }

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

        boolean wasNew = s.getId() == null;

        profileRepository.save(s);

        if ( wasNew && s.getAutoRegister() != null && s.getAutoRegister() ) {
            try {
                userService.generateUser(s, "CLIENT");
            }
            catch(Exception e) {
                throw new BadRequestException("Ошибка регистрации нового клиента!");
            }
        }
        else if ( !wasNew && s.getByFio() != null && s.getByFio() ) {
            try {
                userService.updateUser(s);
            }
            catch(Exception e) {
                throw new BadRequestException("Ошибка сохранения клиента!");
            }
        }

        return s;
    }

    @Override
    public <S extends Profile> S create(S s) {
        s.setDeleted(false);
        return save( s );
    }

    @Override
    public void delete(Long aLong) {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            throw new ForbiddenException("Вам запрещено удалять профили!");

        Profile profile = profileRepository.findById(aLong).orElse(null);

        if ( profile == null )
            throw new ResourceNotFoundException("Профиль не найден!");

        List<ServiceDocument> serviceDocuments = serviceDocumentRepository.findByClientIdOrExecutorIdOrderByNumber( profile.getId(), profile.getId());
        if ( serviceDocuments.size() > 0 ) {
            if ( serviceDocuments.size() > 10 )
                throw new BadRequestException( String.format( "Данный профиль указан в %s заказ-нарядах!", serviceDocuments.size() ) );
            else {
                List<String> documentsNumbers = serviceDocuments.stream().map( ServiceDocument::getNumber ).collect(Collectors.toList() );
                throw new BadRequestException( String.format( "Данный профиль указан в следующих заказ-нарядах: %s!", String.join(",", documentsNumbers) ) );
            }
        }

        profile.setDeleted(true);
        profileRepository.save(profile);
    }
}
