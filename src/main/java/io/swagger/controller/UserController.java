package io.swagger.controller;

import io.swagger.helper.DateHelper;
import io.swagger.helper.UserHelper;
import io.swagger.helper.UserSpecificationBuilder;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.SubscriptionRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import io.swagger.postgres.resourceProcessor.UserResourceProcessor;
import io.swagger.response.api.ApiResponse;
import io.swagger.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/external/users")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserResourceProcessor userResourceProcessor;

    @GetMapping("/currentUser")
    public ResponseEntity getCurrentUser(@RequestParam(value = "include", required = false) List<String> includes) throws Exception {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        return ResponseEntity.ok( userResourceProcessor.toResource( currentUser, includes ) );

    }

    @PutMapping("/{id}")
    public ResponseEntity saveExistingUser(@RequestBody User user,
                                           @PathVariable("id") Long id) {

        User existingUser = userRepository.findById(id).orElse( null );

        if ( existingUser == null )
            return ResponseEntity.status(404).body("Пользователь не найден!");

        if ( user.getPhone() == null || user.getPhone().isEmpty() )
            return ResponseEntity.status(400).body("Телефон не может быть пустым!");

        if ( !userService.isPhoneValid( user.getPhone() ) )
            return ResponseEntity.status(400).body("Неверный номер телефона!");

        if ( user.getEmail() != null && user.getEmail().length() == 0 )
            user.setEmail(null);

        if ( userRepository.isUserExistsPhoneNotSelf( user.getPhone(), existingUser.getId() ) )
            return ResponseEntity.status(400).body("Данный телефон уже указан у другого пользователя!");
        if ( user.getEmail() != null && user.getEmail().length() > 0 &&
                userRepository.isUserExistsEmailNotSelf( user.getEmail(), existingUser.getId() ) )
            return ResponseEntity.status(400).body("Данная почта уже указана у другого пользователя!");

        if ( UserHelper.isClient( user ) ) {
            if ( user.getVin() != null && user.getVin().length() > 0 &&
                    userRepository.isUserExistsVinNotSelf( user.getVin(), existingUser.getId() ) )
                return ResponseEntity.status(400).body("Данный VIN-номер уже указан у другого пользователя!");
        }
        if ( UserHelper.isServiceLeader( user ) ) {
            if ( user.getInn() != null && user.getInn().length() > 0 &&
                    userRepository.isUserExistsInnNotSelf( user.getInn(), existingUser.getId() ) )
                return ResponseEntity.status(400).body("Данный ИНН уже указан у другого пользователя!");
        }

        userService.processPhone(user);

        //TODO: переделать на JSON API
        Long currentSubscriptionId = user.getCurrentCurrentSubscriptionId();
        if ( currentSubscriptionId != null ) {

            Subscription origCurrentSubscription = subscriptionRepository.findById( currentSubscriptionId ).orElse( null );
            if ( origCurrentSubscription != null )
                user.setCurrentSubscription( origCurrentSubscription );
            else
                user.setCurrentSubscription( null );

        }

        user.setPassword( existingUser.getPassword() );
        user.setAccountExpired( existingUser.isAccountExpired() );
        user.setAccountLocked( existingUser.isAccountLocked() );
        user.setCredentialsExpired( existingUser.isCredentialsExpired() );
        user.setBalance( existingUser.getBalance() );
        userRepository.save( user );

        webSocketController.sendCounterRefreshMessage( user, true );

        return ResponseEntity.ok(user);

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") Long userId) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser.getId().equals( userId ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Невозможно удалить активного пользователя!" ) );

        User user = userRepository.findById( userId ).orElse( null );

        if ( user == null )
            return ResponseEntity.status(400).body( new ApiResponse( "Пользователь не найден!" ) );

        user.setEnabled( false );
        userRepository.save( user );

        return ResponseEntity.ok( new ApiResponse( "Пользователь успешно удален!" ) );

    }

    @PostMapping(value = "/{id}/password/change")
    public ResponseEntity changePassword(@PathVariable("id") Long id,
                                         @RequestParam("oldPassword") String oldPassword,
                                         @RequestParam("newPassword") String newPassword,
                                         @RequestParam("rePassword") String rePassword) {

        if ( newPassword == null || newPassword.length() == 0 )
            return ResponseEntity.status(400).body("Новый пароль не указан!");
        if ( rePassword == null || rePassword.length() == 0 )
            return ResponseEntity.status(400).body("Подтверждение пароля не указано!");
        if ( !newPassword.equals( rePassword ) )
            return ResponseEntity.status(400).body("Пароли не совпадают!");
        if ( newPassword.length() < 6 )
            return ResponseEntity.status(400).body("Пароль не может содержать менее 6 символов!");

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isAdmin( currentUser ) ) {

            if ( currentUser.getId().equals( id ) ) {

                if ( oldPassword == null || oldPassword.length() == 0 )
                    return ResponseEntity.status(400).body("Старый пароль не указан!");
                if ( !isPasswordEquals( currentUser, oldPassword ) )
                    return ResponseEntity.status(403).body("Старый и новый пароли не совпадают!");

                currentUser.setPassword( userPasswordEncoder.encode( newPassword ) );
                userRepository.save(currentUser);

            }
            else {

                User user = userRepository.findById(id).orElse( null );
                if ( user == null )
                    return ResponseEntity.status(404).body("Пользователь не найден!");

                user.setPassword( userPasswordEncoder.encode( newPassword ) );
                userRepository.save( user );

            }

        }
        else {
            if ( currentUser.getId().equals( id ) ) {

                if ( oldPassword == null || oldPassword.length() == 0 )
                    return ResponseEntity.status(400).body("Старый пароль не указан!");
                if ( !isPasswordEquals( currentUser, oldPassword ) )
                    return ResponseEntity.status(403).body("Старый и новый пароли не совпадают!");

                currentUser.setPassword( userPasswordEncoder.encode( newPassword ) );
                userRepository.save(currentUser);

            }
            else {
                return ResponseEntity.status(403).body("Вам запрещено изменять пароль этого пользователя!");
            }
        }

        return ResponseEntity.status(200).build();
    }

    private boolean isPasswordEquals(User user, String oldPass) {
        return userPasswordEncoder.matches( oldPass, user.getPassword() );
    }

    @PostMapping(value = "/{id}/role/change")
    public ResponseEntity changeRole(@PathVariable("id") Long id,
                                     @RequestParam("role") String role) {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isAdmin( currentUser ) || currentUser.getId().equals( id ) ) {

            User user;

            if ( currentUser.getId().equals( id ) )
                user = currentUser;
            else
                user = userRepository.findById(id).orElse( null );

            if ( user == null )
                return ResponseEntity.status(404).body( new ApiResponse("Пользователь не найден!") );

            UserRole userRole = userRoleRepository.findByName(role);
            if ( userRole == null )
                return ResponseEntity.status(404).body( new ApiResponse("Роль не найдена!") );

            if ( !UserHelper.isAdmin( currentUser ) && userRole.getName().equals("ADMIN") )
                return ResponseEntity.status(400).body( new ApiResponse("Вам запрещено выбирать данную роль!") );

            boolean isSameRole = user.getRoles().stream().anyMatch(ur -> ur.getId().equals( userRole.getId() ) );
            if ( isSameRole )
                return ResponseEntity.status(400).body( new ApiResponse("Роль уже выбрана!") );

            user.getRoles().clear();
            user.getRoles().add( userRole );

            userRepository.save( user );

            return ResponseEntity.status(200).build();
        }
        else
            return ResponseEntity.status(400).body( new ApiResponse("Вам запрещено изменять роль этого пользователя!") );
    }

    @GetMapping
    public ResponseEntity findAll(JsonApiParams params) throws Exception {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        FilterPayload filterPayload = params.getFilterPayload();
        Pageable pageable = params.getPageable();

        Specification<User> specification = UserSpecificationBuilder.buildSpecification( currentUser, filterPayload );

        return ResponseEntity.ok(
                userResourceProcessor.toResourcePage(
                        userRepository.findAll(specification, pageable), params.getInclude(), userRepository.count(specification), pageable
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") Long id) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        User user = userRepository.findById(id).orElse( null );

        if ( user == null )
            return ResponseEntity.status(404).build();

        if ( currentUser.getId().equals( user.getId() ) ||
                UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.ok( user );
        else
            return ResponseEntity.status(403).build();
    }

    @GetMapping("/eventMessages/fromUsers")
    public ResponseEntity findEventMessagesFromUsers() {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isAdmin( currentUser ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageFromUsersByAdmin() );
        }
        else
            return ResponseEntity.status(404).build();

    }

    @GetMapping("/eventMessages/toUsers")
    public ResponseEntity findEventMessagesToUsers() {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isAdmin( currentUser ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageToUsersByAdmin() );
        }
        else
            return ResponseEntity.status(404).build();

    }

    @GetMapping("/subscribers/expired")
    public ResponseEntity findUsersWithExpiredSubscriptions() {
        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin(currentUser) )
            return ResponseEntity.status(404).build();

        List<User> users = userRepository.findAll();
        List<Map<String, String>> usersWithExpiredSubscriptions = new ArrayList<>();

        Date now = new Date();

        users.forEach(user -> {
            Subscription currentSubscription = user.getCurrentSubscription();

            if ( currentSubscription != null && currentSubscription.getEndDate().before( now ) ) {
                Map<String, String> expired = new HashMap<>();

                expired.put( "fio", user.getFio() );
                expired.put( "date", DateHelper.formatDate( currentSubscription.getEndDate() ) );

                usersWithExpiredSubscriptions.add( expired );
            }
        });

        if ( usersWithExpiredSubscriptions.size() > 0 )
            return ResponseEntity.status(200).body( usersWithExpiredSubscriptions );

        return ResponseEntity.status(404).build();
    }

    @Data
    public static class FilterPayload {

        private String role;
        private Boolean isAutoRegistered;
        private String phone;
        private String email;
        private String fio;
        private String inn;

    }

    @Data
    public static class JsonApiParams {
        private Map<String, List<String>> filter;
        private List<String> sort;
        private List<String> include;
        private Map<String, Integer> page;

        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( filter == null )
                return filterPayload;

            if ( filter.containsKey("role") && filter.get("role").size() > 0 )
                filterPayload.setRole( filter.get("role").get(0) );
            if ( filter.containsKey("isAutoRegistered") && filter.get("isAutoRegistered").size() > 0 )
                filterPayload.setIsAutoRegistered( Boolean.valueOf( filter.get("isAutoRegistered").get(0) ) );
            if ( filter.containsKey("phone") && filter.get("phone").size() > 0 )
                filterPayload.setPhone( filter.get("phone").get(0) );
            if ( filter.containsKey("email") && filter.get("email").size() > 0 )
                filterPayload.setEmail( filter.get("email").get(0) );
            if ( filter.containsKey("fio") && filter.get("fio").size() > 0 )
                filterPayload.setFio( filter.get("fio").get(0) );
            if ( filter.containsKey("inn") && filter.get("inn").size() > 0 )
                filterPayload.setInn( filter.get("inn").get(0) );

            return filterPayload;
        }

        public PageRequest getPageable() {
            int number;
            int size = page.getOrDefault("size", 20);

            if ( !page.containsKey("number") )
                number = 0;
            else
                number = page.get("number") - 1;

            if ( sort == null || sort.size() == 0 )
                return PageRequest.of(number, size);

            String firstField = sort.get(0);
            Sort sortDomain;

            if (firstField.startsWith("-"))
                sortDomain = Sort.by(Sort.Direction.DESC, sort.toArray( new String[ sort.size() ] ));
            else
                sortDomain = Sort.by(Sort.Direction.ASC, sort.toArray( new String[ sort.size() ] ));

            return PageRequest.of(number, size, sortDomain);
        }
    }

}
