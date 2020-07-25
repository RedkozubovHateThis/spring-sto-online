package io.swagger.service.impl;

import io.swagger.controller.WebSocketController;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.Customer;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import io.swagger.service.PasswordGenerationService;
import io.swagger.service.SmsService;
import io.swagger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String PHONE_REGEXP = "^((\\+7|7|8)+([0-9]){10})$";
    private static final String EMAIL_REGEXP = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    @Autowired
    private EventMessageRepository eventMessageRepository;
    @Autowired
    private WebSocketController webSocketController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private PasswordGenerationService passwordGenerationService;
    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Value("${domain.url}")
    private String domainUrl;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @Override
    public String preparePhone(String phone) {
        String preparedPhone = phone.replaceAll("[^+\\d]", "");

        if ( !isPhoneValid( preparedPhone ) ) {
            logger.warn("Phone is not valid, trying to fix...");
            preparedPhone = fixPhone(preparedPhone);

            if ( !isPhoneValid( preparedPhone ) ) {
                logger.error("Phone is not valid after fixing...");
                return null;
            }
        }

        return preparedPhone;

    }

    private String fixPhone(String phone) {

        //Предполагаем, что забыли добавить 8 в начале телефона
        //Все коды регионов в России начинаются с 9
        if ( phone.length() == 10 && phone.charAt(0) == '9' ) {
            return '8' + phone;
        }
        else
            return phone;

    }

    @Override
    public boolean isPhoneValid(String phone) {
        return phone.matches(PHONE_REGEXP);
    }

    @Override
    public boolean isEmailValid(String email) {
        return email.matches(EMAIL_REGEXP);
    }

    @Override
    public void processPhone(Profile profile) {
        String originalPhone = profile.getPhone();

        if ( originalPhone.charAt(0) == '+' ) {
            profile.setPhone( originalPhone.replaceAll("\\+7", "8") );
        }
        else if ( originalPhone.charAt(0) == '7' ) {
            profile.setPhone( originalPhone.replaceFirst("7", "8") );
        }
    }

    @Override
    public void processPhone(Customer customer) {
        String originalPhone = customer.getPhone();

        if ( originalPhone.charAt(0) == '+' ) {
            customer.setPhone( originalPhone.replaceAll("\\+7", "8") );
        }
        else if ( originalPhone.charAt(0) == '7' ) {
            customer.setPhone( originalPhone.replaceFirst("7", "8") );
        }
    }

    @Override
    public String processPhone(String phone) {
        if ( phone.charAt(0) == '+' ) {
            return phone.replaceAll("\\+7", "8");
        }
        else if ( phone.charAt(0) == '7' ) {
            return phone.replaceFirst("7", "8");
        }

        return phone;
    }

    @Override
    public void generateUser(Profile profile) throws Exception {

        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin(currentUser) && !UserHelper.isServiceLeader(currentUser) )
            throw new Exception();

        User user = new User();
        user.setProfile(profile);

        user.setUsername( UUID.randomUUID().toString() );
        user.setEnabled(true);
        user.setIsAutoRegistered(true);

        if ( profile.getByFio() ) {
            user.setFirstName( profile.getFirstName() );
            user.setLastName( profile.getLastName() );
            user.setMiddleName( profile.getMiddleName() );
        }

        String rawPassword = passwordGenerationService.generatePassword();

        user.setPassword( userPasswordEncoder.encode(rawPassword) );

        UserRole userRole = userRoleRepository.findByName("CLIENT");
        if ( userRole != null ) user.getRoles().add( userRole );

        userRepository.save(user);

        if ( !demoDomain ) {
            String smsText = String.format("По вашему автомобилю добавлен новый заказ-наряд. " +
                    "Логин для входа в систему: ваш телефон, пароль: %s. " +
                    "Сервис BUROMOTORS: %s/login", rawPassword, domainUrl);
            logger.info(" [ SCHEDULER ] Prepared sms text: \"{}\"", smsText );

            smsService.sendSmsAsync( profile.getPhone(), smsText );
        }

    }
}
