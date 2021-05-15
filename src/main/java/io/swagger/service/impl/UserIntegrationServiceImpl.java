package io.swagger.service.impl;

import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.integration.IntegrationUser;
import io.swagger.service.UserIntegrationService;
import io.swagger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserIntegrationServiceImpl implements UserIntegrationService {

    private final static Logger logger = LoggerFactory.getLogger(UserIntegrationService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Override
    public void processIntegrationUser(IntegrationUser integrationUser, User user) throws IllegalArgumentException {
        checkAndSetFields(integrationUser);

        UserRole userRole = getRole(integrationUser);
        Profile profile = getProfile(integrationUser, user);

        if ( !integrationUser.getRole().equals( "CLIENT" ) && !isFieldEmpty( profile.getInn() ) ) {
            Boolean isExistsByInn;

            if ( profile.getId() != null )
                isExistsByInn = profileRepository.isProfileExistsInnNotSelf( profile.getInn(), profile.getId() );
            else
                isExistsByInn = profileRepository.isProfileExistsInn( profile.getInn() );

            if ( isExistsByInn )
                throw new IllegalArgumentException("Данный ИНН уже указан у другого пользователя!");
        }

        Boolean isExistsByPhone;

        if ( profile.getId() != null )
            isExistsByPhone = profileRepository.isProfileExistsPhoneNotSelf( profile.getPhone(), profile.getId() );
        else
            isExistsByPhone = profileRepository.isProfileExistsPhone( profile.getPhone() );

        if ( isExistsByPhone )
            throw new IllegalArgumentException("Данный телефон уже указан у другого пользователя!");

        if ( profile.getEmail() != null ) {

            Boolean isExistsByEmail;

            if ( profile.getId() != null )
                isExistsByEmail = profileRepository.isProfileExistsEmailNotSelf( profile.getEmail(), profile.getId() );
            else
                isExistsByEmail = profileRepository.isProfileExistsEmail( profile.getEmail() );

            if ( isExistsByEmail )
                throw new IllegalArgumentException("Данная почта уже указана у другого профиля!");

        }

        User newUser = profile.getUser();
        if ( newUser == null ) {
            newUser = new User();
            newUser.setUsername( UUID.randomUUID().toString() );
            newUser.setEnabled( true );
            newUser.setProfile( profile );
            newUser.getRoles().add( userRole );

            if ( integrationUser.getRole().equals("CLIENT") )
                newUser.setIsAutoRegistered( true );
            else
                newUser.setIsAutoRegistered( false );
        }

        newUser.setPassword( userPasswordEncoder.encode( integrationUser.getPassword() ) );
        newUser.setFirstName( integrationUser.getFirstName() );
        newUser.setLastName( integrationUser.getLastName() );
        newUser.setMiddleName( integrationUser.getMiddleName() );

        if ( integrationUser.getRole().equals( "SERVICE_LEADER" )
                || integrationUser.getRole().equals( "FREELANCER" ) ) {
            newUser.setBankBic( integrationUser.getBankBic() );
            newUser.setBankName( integrationUser.getBankName() );
            newUser.setCheckingAccount( integrationUser.getCheckingAccount() );
            newUser.setCorrAccount( integrationUser.getCorrAccount() );
        }

        profileRepository.save(profile);
        userRepository.save(newUser);
    }

    private UserRole getRole(IntegrationUser integrationUser) {
        logger.info(" [ USER INTEGRATION SERVICE ] Searching for role... ");
        return userRoleRepository.findByName( integrationUser.getRole() );
    }

    private Profile getProfile(IntegrationUser integrationUser, User user) {
        logger.info(" [ USER INTEGRATION SERVICE ] Searching for profile... ");
        Profile profile = profileRepository.findOneByIntegrationId( integrationUser.getIntegrationId() );

        if ( profile == null ) {
            logger.info(" [ USER INTEGRATION SERVICE ] Profile not found, creating new one... ");
            profile = new Profile();
            profile.setDeleted( false );
            profile.setIntegrationId( integrationUser.getIntegrationId() );
            profile.setPhone( integrationUser.getPhone() );
            profile.setEmail( integrationUser.getEmail() );
            profile.setInn( integrationUser.getInn() );

            if ( integrationUser.getRole().equals("CLIENT") && user.getProfile() != null )
                profile.setCreatedBy( user.getProfile() );
        }

        profile.setName( integrationUser.getFullName() );
        profile.setAddress( integrationUser.getAddress() );

        return profile;
    }

    private void checkAndSetFields(IntegrationUser integrationUser) throws IllegalArgumentException {
        if ( isFieldEmpty( integrationUser.getIntegrationId() ) )
            throw new IllegalArgumentException("Не указан идентификатор пользователя");

        if ( isFieldEmpty( integrationUser.getRole() ) )
            throw new IllegalArgumentException("Не указана роль");

        if ( isFieldEmpty( integrationUser.getPhone() ) )
            throw new IllegalArgumentException("Не указан телефон");

        if ( isFieldEmpty( integrationUser.getPassword() ) )
            throw new IllegalArgumentException("Не указан пароль");

        if ( integrationUser.getPassword().length() < 6 )
            throw new IllegalArgumentException("Длина пароля не может быть менее 6 символов");

        if ( isFieldEmpty( integrationUser.getFullName() ) )
            throw new IllegalArgumentException("Не указано полное наименование");

        String oldPhone = integrationUser.getPhone();
        integrationUser.setPhone( userService.processPhone( integrationUser.getPhone() ) );
        if ( !userService.isPhoneValid( integrationUser.getPhone() ) )
            throw new IllegalArgumentException( String.format("Неверный формат телефона: %s", oldPhone) );

        if ( !isFieldEmpty( integrationUser.getEmail() ) && !userService.isEmailValid( integrationUser.getEmail() ) )
            throw new IllegalArgumentException( String.format("Неверный формат email: %s", integrationUser.getEmail()) );

        String role = integrationUser.getRole();
        if ( !role.equals("SERVICE_LEADER") && !role.equals("FREELANCER") && !role.equals("CLIENT") )
            throw new IllegalArgumentException( String.format( "Указана неверная роль: %s", role ) );

        if ( role.equals("SERVICE_LEADER") ) {
            if ( isFieldEmpty( integrationUser.getInn() ) )
                throw new IllegalArgumentException("Не указан ИНН");

            try {
                userService.isInnCorrect( integrationUser.getInn() );
            }
            catch (DataNotFoundException dnfe) {
                throw new IllegalArgumentException( String.format( "Указан неверный ИНН: %s", integrationUser.getInn() ) );
            }
            catch (Exception e) {
                throw new IllegalArgumentException( "Ошибка проверки ИНН, повторите попытку позже" );
            }
        }
    }

    private Boolean isFieldEmpty(String field) {
        return field == null || field.length() == 0;
    }
}
