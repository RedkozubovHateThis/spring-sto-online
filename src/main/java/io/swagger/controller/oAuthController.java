package io.swagger.controller;

import com.sun.javaws.exceptions.InvalidArgumentException;
import io.swagger.firebird.model.Organization;
import io.swagger.firebird.repository.OrganizationRepository;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/oauth")
@RestController
public class oAuthController {

    private static final Logger logger = LoggerFactory.getLogger(oAuthController.class);
    private final String PHONE_REGEXP = "^((\\+7|7|8)+([0-9]){10})$";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @PostMapping("/register/{roleName}")
    public ResponseEntity register(@RequestBody User user,
                                   @PathVariable("roleName") String roleName) {

        if ( user == null )
            return ResponseEntity.status(400).body("Тело запроса не может быть пустым!");

        if ( roleName == null )
            return ResponseEntity.status(400).body("Роль не может быть пустой!");

        if ( user.getPassword() == null || user.getPassword().isEmpty() )
            return ResponseEntity.status(400).body("Пароль не может быть пустым!");

        if ( user.getEmail() == null || user.getEmail().isEmpty() )
            return ResponseEntity.status(400).body("Почта не может быть пустой!");

        if ( user.getPhone() == null || user.getPhone().isEmpty() )
            return ResponseEntity.status(400).body("Телефон не может быть пустым!");

        if ( !isPhoneValid( user.getPhone() ) )
            return ResponseEntity.status(400).body("Неверный номер телефона!");

        processPhone(user);

        if ( user.getUsername() != null && user.getUsername().length() > 0 &&
                userRepository.isUserExistsUsername( user.getUsername() ) )
            return ResponseEntity.status(400).body("Пользователь с таким логином уже существует!");

        if ( userRepository.isUserExistsPhone( user.getPhone() ) )
            return ResponseEntity.status(400).body("Пользователь с таким телефоном уже существует!");

        if ( userRepository.isUserExistsEmail( user.getEmail() ) )
            return ResponseEntity.status(400).body("Пользователь с такой почтой уже существует!");

        if ( user.getUsername() == null || user.getUsername().isEmpty() )
            user.setUsername( UUID.randomUUID().toString() );

        user.setEnabled(true);
        user.setIsApproved(false);
        user.setInVacation(false);
        user.setPassword( userPasswordEncoder.encode( user.getPassword() ) );

        UserRole clientRole = userRoleRepository.findByName(roleName);

        if ( clientRole != null )
            user.getRoles().add(clientRole);

        if ( roleName.equals("CLIENT") )
            setModerator(user);
        else if ( roleName.equals("SERVICE_LEADER") ) {
            if ( user.getInn() == null || user.getInn().isEmpty() )
                return ResponseEntity.status(400).body("ИНН не может быть пустым!");

            if ( userRepository.isUserExistsInn( user.getInn() ) )
                return ResponseEntity.status(400).body("Пользователь с таким ИНН уже существует!");

            setModerator(user);
        }

        userRepository.save(user);

        return ResponseEntity.ok().build();

    }

    @GetMapping("/demo/register")
    public ResponseEntity registerDemo() {

        if ( !demoDomain ) return ResponseEntity.status(401).build();

        long usersCount = userRepository.countAll();
        String password = UUID.randomUUID().toString();
        String email = String.format( "demo_user%s@buromotors.ru", usersCount );

        User user = new User();
        user.setFirstName("Пользователь");
        user.setLastName("Демонстрационный");
        user.setPassword( password );
        user.setEmail( email );
        user.setPhone( buildDemoPhone( usersCount ) );

        if ( user.getUsername() == null || user.getUsername().isEmpty() )
            user.setUsername( UUID.randomUUID().toString() );

        user.setEnabled(true);
        user.setInVacation(false);
        user.setPassword( userPasswordEncoder.encode( user.getPassword() ) );

        UserRole clientRole = userRoleRepository.findByName("SERVICE_LEADER");

        if ( clientRole != null )
            user.getRoles().add(clientRole);

        Organization organization = organizationRepository.findOne(1);

        if ( organization != null ) {
            user.setInn( organization.getInn() );
            user.setOrganizationId( organization.getId() );
            user.setIsApproved(true);
        }

        setModerator(user);

        userRepository.save(user);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", email);
        credentials.put("password", password);

        return ResponseEntity.ok(credentials);

    }

    private String buildDemoPhone(long usersCount) {
        String lastNumbers = String.valueOf( usersCount );
        StringBuilder sb = new StringBuilder("8");

        for ( int z = 0; z < 11 - lastNumbers.length(); z++ ) {
            sb.append("0");
        }

        sb.append(lastNumbers);

        return sb.toString();
    }

    private boolean isPhoneValid(String phone) {
        return phone.matches(PHONE_REGEXP);
    }

    private void processPhone(User user) {
        String originalPhone = user.getPhone();

        if ( originalPhone.charAt(0) == '+' ) {
            user.setPhone( originalPhone.replaceAll("\\+7", "8") );
        }
        else if ( originalPhone.charAt(0) == '7' ) {
            user.setPhone( originalPhone.replaceFirst("7", "8") );
        }
    }

    private void setModerator(User user) {

        List<User> moderators = userRepository.findUsersByRoleName("MODERATOR");

        User userModerator = null;

        for (User moderator : moderators) {

            if ( userModerator == null )
                userModerator = moderator;
            else {

                if ( moderator.getLastUserAcceptDate() == null ) {
                    userModerator = moderator;
                    break;
                }
                else if ( userModerator.getLastUserAcceptDate() == null ) {
                    break;
                }
                else if ( moderator.getLastUserAcceptDate().before( userModerator.getLastUserAcceptDate() ) ) {
                    userModerator = moderator;
                }

            }

        }

        if ( userModerator != null ) {
            logger.info( "Got moderator \"{}\" for user \"{}\"", userModerator.getFio(), user.getFio() );
            user.setModeratorId( userModerator.getId() );
            webSocketController.sendCounterRefreshMessage( userModerator.getId() );

            userModerator.setLastUserAcceptDate( new Date() );
            userRepository.save( userModerator );
        }
        else {
            logger.error("Moderator not found");
        }

    }

}
