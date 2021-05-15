package io.swagger.controller;

import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import io.swagger.response.RegisterModel;
import io.swagger.response.api.ApiResponse;
import io.swagger.response.api.PasswordRestoreData;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.service.MailSendService;
import io.swagger.service.PasswordGenerationService;
import io.swagger.service.SmsService;
import io.swagger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/oauth")
@RestController
public class oAuthController {

    private static final Logger logger = LoggerFactory.getLogger(oAuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private PasswordGenerationService passwordGenerationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private MailSendService mailSendService;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    private Map<String, PasswordRestoreData> hashPool = new HashMap<>();

    @PostMapping("/register/{roleName}")
    public ResponseEntity register(@RequestBody RegisterModel registerModel,
                                   @PathVariable("roleName") String roleName) {

        if ( registerModel == null )
            return ResponseEntity.status(400).body( new ApiResponse( "Тело запроса не может быть пустым!" ) );

        if ( roleName == null )
            return ResponseEntity.status(400).body( new ApiResponse( "Роль не может быть пустой!" ) );

        if ( !roleName.equals("SERVICE_LEADER") && !roleName.equals("CLIENT") && !roleName.equals("FREELANCER") )
            return ResponseEntity.status(400).body( new ApiResponse( "Неправильная роль!" ) );

        if ( registerModel.getPassword() == null || registerModel.getPassword().isEmpty() )
            return ResponseEntity.status(400).body( new ApiResponse( "Пароль не может быть пустым!" ) );

        if ( registerModel.getPassword().length() < 6 )
            return ResponseEntity.status(400).body( new ApiResponse( "Пароль не может содержать менее 6 символов!" ) );

        if ( !registerModel.getPassword().equals( registerModel.getRePassword() ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Пароли не совпадают!" ) );

        if ( registerModel.getEmail() != null && registerModel.getEmail().length() == 0 )
            registerModel.setEmail(null);

        if ( registerModel.getEmail() != null && registerModel.getEmail().length() > 0 &&
                !userService.isEmailValid( registerModel.getEmail() ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Неверный формат почты!" ) );

        if ( registerModel.getPhone() == null || registerModel.getPhone().isEmpty() )
            return ResponseEntity.status(400).body( new ApiResponse( "Телефон не может быть пустым!" ) );

        if ( !userService.isPhoneValid( registerModel.getPhone() ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Неверный номер телефона!" ) );

        String phone = userService.processPhone( registerModel.getPhone() );

        if ( profileRepository.isProfileExistsPhone( phone ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Пользователь с таким телефоном уже существует!" ) );

        if ( registerModel.getEmail() != null && registerModel.getEmail().length() > 0 &&
                profileRepository.isProfileExistsEmail( registerModel.getEmail() ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Пользователь с такой почтой уже существует!" ) );

        User user = new User();
        Profile profile = new Profile();

        user.setProfile( profile );
        user.setUsername( UUID.randomUUID().toString() );
        user.setFirstName( registerModel.getFirstName() );
        user.setMiddleName( registerModel.getMiddleName() );
        user.setLastName( registerModel.getLastName() );
        user.setEnabled(true);
        user.setIsAutoRegistered(false);
        user.setPassword( userPasswordEncoder.encode( registerModel.getPassword() ) );

        profile.setPhone( phone );
        profile.setEmail( registerModel.getEmail() );
        profile.setAddress( registerModel.getAddress() );
        profile.setInn( registerModel.getInn() );
        profile.setName( registerModel.getName() );
        profile.setDeleted( false );

        UserRole role = userRoleRepository.findByName(roleName);

        if ( role != null )
            user.getRoles().add(role);

        if ( roleName.equals("SERVICE_LEADER") || roleName.equals("FREELANCER") ) {
            if ( roleName.equals("SERVICE_LEADER") && ( registerModel.getInn() == null || registerModel.getInn().isEmpty() ) )
                return ResponseEntity.status(400).body( new ApiResponse( "ИНН не может быть пустым!" ) );

            if ( registerModel.getInn() != null && !registerModel.getInn().isEmpty()
                    && profileRepository.isProfileExistsInn( registerModel.getInn() ) )
                return ResponseEntity.status(400).body( new ApiResponse( "Пользователь с таким ИНН уже существует!" ) );

            try {
                if ( roleName.equals("SERVICE_LEADER") )
                    userService.isInnCorrect( registerModel.getInn() );
            } catch (DataNotFoundException e) {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }

        profileRepository.save(profile);
        userRepository.save(user);

        return ResponseEntity.ok( new ApiResponse( "Регистрация успешно завершена!" ) );

    }

    @PostMapping("/restore")
    public ResponseEntity restore(@RequestParam("restoreData") String restoreData) {

        if ( restoreData == null || restoreData.length() == 0 )
            return ResponseEntity.status(400).body("Почта не может быть пустой!");

        //Если данные для восстановления проходят как валидный телефон, то отправляем смс
        if ( userService.isPhoneValid( restoreData ) ) {
            String processedPhone = userService.processPhone(restoreData);

            User user = userRepository.findByPhone( processedPhone );

            if ( user == null )
                return ResponseEntity.status(400).body( new ApiResponse ( "Пользователя с таким телефоном не существует!" ) );

            String newPassword = passwordGenerationService.generatePassword();

            user.setPassword( userPasswordEncoder.encode( newPassword ) );
            userRepository.save( user );

            String smsText = String.format( "Ваш новый пароль для доступа к BUROMOTORS: %s", newPassword );
            smsService.sendSms( processedPhone, smsText );

            return ResponseEntity.ok( new ApiResponse( "Ваш новый пароль отправлен вам на телефон!" ) );
        }
        //Если нет, то пытаемся восстановить по почте
        else if ( userService.isEmailValid( restoreData ) ) {

            User user = userRepository.findByEmail( restoreData );

            if ( user == null )
                return ResponseEntity.status(400).body( new ApiResponse ( "Пользователя с такой почтой не существует!" ) );

            String uuid = UUID.randomUUID().toString();

            PasswordRestoreData passwordRestoreData = new PasswordRestoreData(user);
            hashPool.put( uuid, passwordRestoreData );

            mailSendService.sendPasswordRestoreMail( restoreData, "Восстановление пароля к BUROMOTORS", uuid );

            return ResponseEntity.ok( new ApiResponse( "Ссылка на страницу восстановления пароля отправлена вам на почту!" ) );
        }
        else
            return ResponseEntity.status(400).body( new ApiResponse ( "Неверные данные для восстановления пароля!" ) );

    }

    @PostMapping("/restore/password")
    public ResponseEntity restore(@RequestParam("password") String password,
                                  @RequestParam("rePassword") String rePassword,
                                  @RequestParam("hash") String hash) {

        if ( hash == null || hash.length() == 0 ||
                !hashPool.containsKey( hash ) || !hashPool.get( hash ).isValid() )
            return ResponseEntity.status(400).body( new ApiResponse( "Ссылка недействительна!" ) );

        if ( password == null || password.length() == 0 )
            return ResponseEntity.status(400).body( new ApiResponse( "Пароль не может быть пустым!" ) );
        if ( rePassword == null || rePassword.length() == 0 )
            return ResponseEntity.status(400).body( new ApiResponse( "Подтверждение пароля не может быть пустым!" ) );

        if ( password.length() < 6 )
            return ResponseEntity.status(400).body( new ApiResponse( "Пароль не может содержать менее 6 символов!" ) );
        if ( !password.equals( rePassword ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Пароли не совпадают!" ) );

        PasswordRestoreData passwordRestoreData = hashPool.remove( hash );

        User user = passwordRestoreData.getUser();

        user.setPassword( userPasswordEncoder.encode( password ) );
        userRepository.save( user );

        return ResponseEntity.ok( new ApiResponse( "Пароль успешно восстановлен!" ) );

    }

    @GetMapping("/demo/register")
    public ResponseEntity registerDemo() {

        if ( !demoDomain ) return ResponseEntity.status(401).build();

        long usersCount = userRepository.countAll();
        String password = UUID.randomUUID().toString();
        String email = String.format( "demo_user%s@buromotors.ru", usersCount );

        User user = new User();
        Profile profile = new Profile();

        user.setProfile( profile );
        user.setFirstName("Пользователь");
        user.setLastName("Демонстрационный");
        user.setPassword( password );
        profile.setEmail( email );
        profile.setPhone( buildDemoPhone( usersCount ) );
        profile.setName( "Демонстрационный Пользователь" );
        profile.setDeleted( false );

        if ( user.getUsername() == null || user.getUsername().isEmpty() )
            user.setUsername( UUID.randomUUID().toString() );

        user.setEnabled(true);
        user.setIsAutoRegistered(false);
        user.setPassword( userPasswordEncoder.encode( user.getPassword() ) );
        user.setBalance(25000.0);

        UserRole clientRole = userRoleRepository.findByName("SERVICE_LEADER");

        if ( clientRole != null )
            user.getRoles().add(clientRole);

        profileRepository.save( profile );
        userRepository.save(user);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", email);
        credentials.put("password", password);

        return ResponseEntity.ok(credentials);

    }

    private String buildDemoPhone(long usersCount) {
        String lastNumbers = String.valueOf( usersCount );
        StringBuilder sb = new StringBuilder("8");

        for ( int z = 0; z < 10 - lastNumbers.length(); z++ ) {
            sb.append("0");
        }

        sb.append(lastNumbers);

        return sb.toString();
    }

}
