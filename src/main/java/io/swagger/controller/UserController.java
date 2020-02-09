package io.swagger.controller;

import io.swagger.firebird.model.ModelLink;
import io.swagger.firebird.repository.ModelDetailRepository;
import io.swagger.firebird.repository.ModelLinkRepository;
import io.swagger.helper.UserHelper;
import io.swagger.helper.UserSpecificationBuilder;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.EventMessageRepository;
import io.swagger.postgres.repository.SubscriptionRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import io.swagger.response.api.ApiResponse;
import io.swagger.response.api.EventMessageStatus;
import io.swagger.response.firebird.UserResponse;
import io.swagger.response.firebird.VehicleResponse;
import io.swagger.service.EventMessageService;
import io.swagger.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/secured/users")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private io.swagger.firebird.repository.UserRepository firebirdUserRepository;

    @Autowired
    private ModelLinkRepository modelLinkRepository;

    @Autowired
    private ModelDetailRepository modelDetailRepository;

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private EventMessageService eventMessageService;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private EventMessageRepository eventMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @GetMapping("/currentUser")
    public ResponseEntity getCurrentUser() {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        return ResponseEntity.ok( currentUser );

    }

    @PutMapping("{id}")
    public ResponseEntity saveExistingUser(@RequestBody User user,
                                           @PathVariable("id") Long id) {

        User existingUser = userRepository.findOne(id);

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

        User currentUser = userRepository.findCurrentUser();
        boolean sendMessage = false;
        User targetUser = null;
        EventMessageStatus eventMessageStatus = null;
        boolean sendEventMessage = false;
        MessageType eventMessageType = null;

        if ( UserHelper.isClient( user ) && user.getModeratorId() != null )
            eventMessageStatus = isClientIdChanged(user, existingUser);

        if ( UserHelper.isServiceLeaderOrFreelancer( user ) && user.getModeratorId() != null )
            eventMessageStatus = isOrganizationIdChanged(user, existingUser);

        if ( UserHelper.isAdminOrModerator( currentUser ) &&
                ( UserHelper.isClient( user ) || UserHelper.isServiceLeaderOrFreelancer( user ) ) ) {

            if ( !existingUser.getIsApproved() && user.getIsApproved() ) {
                eventMessageType = MessageType.USER_APPROVE;
                sendEventMessage = true;
            }
            else if ( existingUser.getIsApproved() && !user.getIsApproved() ) {
                eventMessageType = MessageType.USER_REJECT;
                sendEventMessage = true;
            }

        }

        Long replacementModeratorId = user.getCurrentReplacementModeratorId();
        if ( replacementModeratorId != null ) {

            User origReplacementModerator = userRepository.findOne( replacementModeratorId );
            if ( origReplacementModerator != null ) {

                User existingReplacementModerator = existingUser.getReplacementModerator();

                if ( existingReplacementModerator == null ||
                        !existingReplacementModerator.getId().equals( origReplacementModerator.getId() ) ) {
                    targetUser = origReplacementModerator;
                    sendMessage = true;
                }

                user.setReplacementModerator( origReplacementModerator );
            }
            else user.setReplacementModerator( null );

        }

        Long moderatorId = user.getCurrentModeratorId();
        if ( moderatorId != null ) {

            User origModerator = userRepository.findOne( moderatorId );
            if ( origModerator != null )
                user.setModerator( origModerator );
            else
                user.setModerator( null );

        }
        Long currentSubscriptionId = user.getCurrentCurrentSubscriptionId();
        if ( currentSubscriptionId != null ) {

            Subscription origCurrentSubscription = subscriptionRepository.findOne( currentSubscriptionId );
            if ( origCurrentSubscription != null )
                user.setCurrentSubscription( origCurrentSubscription );
            else
                user.setCurrentSubscription( null );

        }

        user.setPassword( existingUser.getPassword() );
        user.setAccountExpired( existingUser.isAccountExpired() );
        user.setAccountLocked( existingUser.isAccountLocked() );
        user.setCredentialsExpired( existingUser.isCredentialsExpired() );
        user.setDocumentUserState( existingUser.getDocumentUserState() );
        user.setBalance( existingUser.getBalance() );
        userRepository.save( user );

        webSocketController.sendCounterRefreshMessage( user, true, true );

        if ( sendMessage ) {
            eventMessageService.buildModeratorReplacementMessage( currentUser, targetUser );
        }
        if ( eventMessageStatus != null && eventMessageStatus.getSending() ) {
            buildAutodealerEventMessage(eventMessageStatus.getTargetUser(), user);
        }
        if ( sendEventMessage ) {
            buildAutodealerApproveEventMessage(user, currentUser, eventMessageType);
        }

        return ResponseEntity.ok(user);

    }

    private EventMessageStatus isClientIdChanged(User user, User existingUser) {
        Integer existingId = existingUser.getClientId();
        Integer newId = user.getClientId();

        return isIdChanged(user, existingId, newId);
    }

    private EventMessageStatus isOrganizationIdChanged(User user, User existingUser) {
        Integer existingId = existingUser.getOrganizationId();
        Integer newId = user.getOrganizationId();

        return isIdChanged(user, existingId, newId);
    }

    private EventMessageStatus isIdChanged(User user, Integer existingId, Integer newId) {
        EventMessageStatus eventMessageStatus = new EventMessageStatus();

        boolean setNewClientId = existingId == null && newId != null;
        boolean changeClientId = existingId != null && newId != null &&
                !existingId.equals( newId );

        if ( setNewClientId || changeClientId ) {

            eventMessageStatus.setTargetUser( userRepository.findOne( user.getModeratorId() ) );
            eventMessageStatus.setSendEventMessage( true );

        }

        return eventMessageStatus;
    }

    private void buildAutodealerEventMessage(User targetUser, User sendUser) {

        EventMessage eventMessage = new EventMessage();
        eventMessage.setSendUser( sendUser );
        eventMessage.setTargetUser( targetUser );
        eventMessage.setMessageType( MessageType.USER_AUTODEALER );
        eventMessage.setMessageDate( new Date() );

        eventMessageRepository.save(eventMessage);

        webSocketController.sendEventMessage( eventMessage, targetUser.getId() );

    }

    private void buildAutodealerApproveEventMessage(User targetUser, User sendUser, MessageType messageType) {

        EventMessage eventMessage = new EventMessage();
        eventMessage.setSendUser( sendUser );
        eventMessage.setTargetUser( targetUser );
        eventMessage.setMessageType( messageType );
        eventMessage.setMessageDate( new Date() );

        eventMessageRepository.save(eventMessage);

        webSocketController.sendEventMessage( eventMessage, targetUser.getId() );

    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") Long userId) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser.getId().equals( userId ) )
            return ResponseEntity.status(400).body( new ApiResponse( "Невозможно удалить активного пользователя!" ) );

        User user = userRepository.findOne( userId );

        if ( user == null )
            return ResponseEntity.status(400).body( new ApiResponse( "Пользователь не найден!" ) );

        user.setEnabled( false );
        userRepository.save( user );

        if ( UserHelper.isModerator( user ) ) {

            List<User> users = userRepository.findAllByModeratorId( user.getId() );

            for (User userWOModerator : users) {
                userWOModerator.setModerator( null );
                userService.setModerator( userWOModerator );
            }

            userRepository.save( users );

        }

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

                User user = userRepository.findOne(id);
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
                user = userRepository.findOne(id);

            if ( user == null )
                return ResponseEntity.status(404).body( new ApiResponse("Пользователь не найден!") );

            UserRole userRole = userRoleRepository.findByName(role);
            if ( userRole == null )
                return ResponseEntity.status(404).body( new ApiResponse("Роль не найдена!") );

            if ( !UserHelper.isAdmin( currentUser ) &&
                    ( userRole.getName().equals("ADMIN") || userRole.getName().equals("MODERATOR") ) )
                return ResponseEntity.status(400).body( new ApiResponse("Вам запрещено выбирать данную роль!") );

            boolean isSameRole = user.getRoles().stream().anyMatch(ur -> ur.getId().equals( userRole.getId() ) );
            if ( isSameRole )
                return ResponseEntity.status(400).body( new ApiResponse("Роль уже выбрана!") );

            if ( !UserHelper.isAdminOrModerator( user ) ) {
                user.setClientId( null );
                user.setOrganizationId( null );
                user.setManagerId( null );
                user.setIsApproved( false );
            }

            user.getRoles().clear();
            user.getRoles().add( userRole );

            userRepository.save( user );

            return ResponseEntity.status(200).build();
        }
        else
            return ResponseEntity.status(400).body( new ApiResponse("Вам запрещено изменять роль этого пользователя!") );
    }

    @GetMapping("/findAll")
    public ResponseEntity findAll(Pageable pageable, FilterPayload filterPayload) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        if ( !UserHelper.isAdminOrModerator( currentUser ) )
            return ResponseEntity.status(403).build();

        Specification<User> specification = UserSpecificationBuilder.buildSpecification( currentUser, filterPayload );

        return ResponseEntity.ok( userRepository.findAll(specification, pageable) );
    }

    @GetMapping("/findReplacementModerators")
    public ResponseEntity findReplacementModerators() {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();
        if ( !UserHelper.isModerator( currentUser ) ) return ResponseEntity.status(404).build();

        return ResponseEntity.ok( userRepository.findUsersByRoleNameExceptId( "MODERATOR", currentUser.getId() ) );

    }

    @GetMapping("/findModerators")
    public ResponseEntity findModerators() {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();
        if ( !UserHelper.isAdmin( currentUser ) ) return ResponseEntity.status(404).build();

        return ResponseEntity.ok( userRepository.findUsersByRoleName("MODERATOR") );

    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable("id") Long id) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        User user = userRepository.findOne(id);

        if ( user == null )
            return ResponseEntity.status(404).build();

        if ( currentUser.getId().equals( user.getId() ) ||
                UserHelper.isAdmin( currentUser ) ||
                ( UserHelper.isModerator( currentUser ) && user.getModeratorId() != null && user.getModeratorId().equals( currentUser.getId() ) ) )
            return ResponseEntity.ok( user );
        else
            return ResponseEntity.status(403).build();
    }

    @GetMapping("/{id}/vehicles")
    public ResponseEntity findVehicles(@PathVariable("id") Long id) {

        User user = userRepository.findOne(id);

        if ( user == null )
            return ResponseEntity.status(404).build();

        if ( user.getVinNumbers() == null || user.getVinNumbers().length == 0 )
            return ResponseEntity.status(404).build();

        List<ModelLink> modelLinks = modelLinkRepository.findByVinNumbers( Arrays.asList( user.getVinNumbers() ) );

        List<VehicleResponse> responses = modelLinks.stream().map(VehicleResponse::new).collect( Collectors.toList() );

        return ResponseEntity.ok( responses );
    }

    @GetMapping("/vehicles/{vinNumber}")
    public ResponseEntity findVehicle(@PathVariable("vinNumber") String vinNumber) {

        if ( vinNumber == null || vinNumber.length() == 0 )
            return ResponseEntity.status(400).body( new ApiResponse( "VIN-номер автомобиля не может быть пустым!" ) );

        List<ModelLink> modelLinks = modelLinkRepository.findByVinNumber( vinNumber );

        if ( modelLinks.size() == 0 )
            return ResponseEntity.status(400).body( new ApiResponse( "Автомобиль с данным VIN-номером не найден!" ) );

        VehicleResponse response = new VehicleResponse( modelLinks.get(0) );

        return ResponseEntity.ok( response );
    }

    @GetMapping("/eventMessages/fromUsers")
    public ResponseEntity findEventMessagesFromUsers() {

        User currentUser = userRepository.findCurrentUser();

        if ( UserHelper.isAdmin( currentUser ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageFromUsersByAdmin() );
        }
        else if ( UserHelper.isModerator( currentUser ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageFromUsers( currentUser.getId() ) );
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
        else if ( UserHelper.isModerator( currentUser ) ) {
            return ResponseEntity.ok( userRepository.findEventMessageToUsers( currentUser.getId() ) );
        }
        else
            return ResponseEntity.status(404).build();

    }

    @GetMapping("/firebird/findAll")
    public ResponseEntity findFirebirdUsers() {

        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdminOrModerator( currentUser ) )
            return ResponseEntity.status(404).build();

        List<io.swagger.firebird.model.User> users = firebirdUserRepository.findAllVisibleUsers();
        List<UserResponse> responses = new ArrayList<>();

        for (io.swagger.firebird.model.User user : users) {

            try {
                responses.add( new UserResponse( user ) );
            }
            catch(IllegalArgumentException ignored) {}

        }

        return ResponseEntity.ok( responses );
    }

    @Deprecated
    @PostMapping("/fillVehicles")
    public ResponseEntity fillVehicles() {

        User currentUser = userRepository.findCurrentUser();

        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.status(403).build();

        List<User> clients = userRepository.findUsersByRoleName("CLIENT");
        Map<Long, String[]> response = new HashMap<>();

        for (User client : clients) {

            if ( !client.isClientValid() ) continue;

            String[] vinNumbers = modelDetailRepository.findVinNumbers( client.getClientId() );

            if ( vinNumbers.length == 0 ) continue;

            client.setVinNumbers( vinNumbers );
            userRepository.save( client );

            response.put( client.getId(), vinNumbers );

        }

        return ResponseEntity.ok(response);
    }

    @Data
    public static class FilterPayload {

        private String role;
        private Boolean isApproved;
        private Boolean isAutoRegistered;
        private String phone;
        private String email;
        private String fio;
        private String inn;

    }

}
