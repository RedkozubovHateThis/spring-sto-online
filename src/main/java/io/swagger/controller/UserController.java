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
import io.swagger.response.api.JsonApiParamsBase;
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
import java.util.stream.Collectors;

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

//        if ( user.getPhone() == null || user.getPhone().isEmpty() )
//            return ResponseEntity.status(400).body("Телефон не может быть пустым!");

//        if ( !userService.isPhoneValid( user.getPhone() ) )
//            return ResponseEntity.status(400).body("Неверный номер телефона!");

//        if ( user.getEmail() != null && user.getEmail().length() == 0 )
//            user.setEmail(null);

//        if ( userRepository.isUserExistsPhoneNotSelf( user.getPhone(), existingUser.getId() ) )
//            return ResponseEntity.status(400).body("Данный телефон уже указан у другого пользователя!");
//        if ( user.getEmail() != null && user.getEmail().length() > 0 &&
//                userRepository.isUserExistsEmailNotSelf( user.getEmail(), existingUser.getId() ) )
//            return ResponseEntity.status(400).body("Данная почта уже указана у другого пользователя!");

//        if ( UserHelper.isServiceLeader( user ) ) {
//            if ( user.getInn() != null && user.getInn().length() > 0 &&
//                    userRepository.isUserExistsInnNotSelf( user.getInn(), existingUser.getId() ) )
//                return ResponseEntity.status(400).body("Данный ИНН уже указан у другого пользователя!");
//        }

//        userService.processPhone(user);

        user.setPassword( existingUser.getPassword() );
        user.setBalance( existingUser.getBalance() );
        userRepository.save( user );

        webSocketController.sendCounterRefreshMessage( user );
        webSocketController.sendCounterRefreshMessageToAdmins();

        return ResponseEntity.ok(user);

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
    public static class JsonApiParams extends JsonApiParamsBase<FilterPayload> {
        public FilterPayload getFilterPayload() {
            FilterPayload filterPayload = new FilterPayload();

            if ( getFilter() == null )
                return filterPayload;

            if ( getFilter().containsKey("role") && getFilter().get("role").size() > 0 )
                filterPayload.setRole( getFilter().get("role").get(0) );
            if ( getFilter().containsKey("isAutoRegistered") && getFilter().get("isAutoRegistered").size() > 0 )
                filterPayload.setIsAutoRegistered( Boolean.valueOf( getFilter().get("isAutoRegistered").get(0) ) );
            if ( getFilter().containsKey("phone") && getFilter().get("phone").size() > 0 )
                filterPayload.setPhone( getFilter().get("phone").get(0) );
            if ( getFilter().containsKey("email") && getFilter().get("email").size() > 0 )
                filterPayload.setEmail( getFilter().get("email").get(0) );
            if ( getFilter().containsKey("fio") && getFilter().get("fio").size() > 0 )
                filterPayload.setFio( getFilter().get("fio").get(0) );
            if ( getFilter().containsKey("inn") && getFilter().get("inn").size() > 0 )
                filterPayload.setInn( getFilter().get("inn").get(0) );

            return filterPayload;
        }
    }

}
