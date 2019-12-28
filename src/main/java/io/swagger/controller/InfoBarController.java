package io.swagger.controller;

import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.model.Organization;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.firebird.repository.ModelDetailRepository;
import io.swagger.firebird.repository.ModelRepository;
import io.swagger.firebird.repository.OrganizationRepository;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.enums.SubscriptionType;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.info.ClientInfo;
import io.swagger.response.info.ModeratorInfo;
import io.swagger.response.info.ServiceLeaderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/secured/infoBar")
public class InfoBarController {

    private final String SUBSCRIPTION_NAME = "Проф";
    private final double SUBSCRIPTION_COST = 16000.0;
    private final double BALANCE = 18765.00;
    private final int TOTAL_DOCUMENTS = 80;
    private final int COMPLETE_DOCUMENTS = 25;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentServiceDetailRepository documentsRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @GetMapping("/client")
    public ResponseEntity getClientInfo() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole(currentUser, "CLIENT") ||
                currentUser.getClientId() == null || !currentUser.getIsApproved() )
            return ResponseEntity.notFound().build();

        ClientInfo clientInfo = new ClientInfo();

        clientInfo.setTotalDocuments( documentsRepository.countByClientId( currentUser.getClientId() ) );
        clientInfo.setTotalDone( documentsRepository.countByClientIdAndState( currentUser.getClientId(), 4 ) );
        clientInfo.setTotalDraft( documentsRepository.countByClientIdAndState( currentUser.getClientId(), 2 ) );
        clientInfo.setTotalRepairSum( calculateTotalRepairSum( currentUser ) );
        clientInfo.setTotalVehicles( modelRepository.countVehiclesByClientId( currentUser.getClientId() ) );
        clientInfo.setTotalServices( organizationRepository.countServicesByClientId( currentUser.getClientId() ) );

        return ResponseEntity.ok( clientInfo );
    }

    @GetMapping("/serviceLeader")
    public ResponseEntity getServiceLeaderInfo() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole(currentUser, "SERVICE_LEADER") ||
                currentUser.getOrganizationId() == null || !currentUser.getIsApproved() )
            return ResponseEntity.notFound().build();

        ServiceLeaderInfo serviceLeaderInfo = new ServiceLeaderInfo();

        if ( demoDomain ) {
            Date subscriptionEndDate = new Date( System.currentTimeMillis() + ( 1000L * 60L * 60L * 24L * 30L ) );

            serviceLeaderInfo.setDocumentsRemains( TOTAL_DOCUMENTS - COMPLETE_DOCUMENTS );
            serviceLeaderInfo.setTotalDocuments( TOTAL_DOCUMENTS );
            serviceLeaderInfo.setSubscribeName( SUBSCRIPTION_NAME );

            serviceLeaderInfo.setSubscribeEndDate( subscriptionEndDate );

            serviceLeaderInfo.setBalance( BALANCE );
            serviceLeaderInfo.setBalanceValid( true );
        }
        else {

            Subscription subscription = currentUser.getCurrentSubscription();

            if ( subscription != null ) {

                Integer documentsCount =
                        documentsRepository.countDocumentsByOrganizationIdAndDates( currentUser.getOrganizationId(),
                                subscription.getStartDate(), subscription.getEndDate() );

                serviceLeaderInfo.setDocumentsRemains( Math.max( subscription.getDocumentsCount() - documentsCount, 0 ) );
                serviceLeaderInfo.setTotalDocuments( subscription.getDocumentsCount() );
                serviceLeaderInfo.setSubscribeName( subscription.getName() );
                serviceLeaderInfo.setSubscribeEndDate( subscription.getEndDate() );

                SubscriptionType renewalType = currentUser.getSubscriptionType();

                if ( renewalType == null )
                    serviceLeaderInfo.setBalanceValid(
                            subscription.getIsRenewable() && currentUser.getBalance() - subscription.getRenewalCost() > 0
                    );
                else
                    serviceLeaderInfo.setBalanceValid(
                            currentUser.getBalance() - renewalType.getCost() > 0
                    );

            }

            serviceLeaderInfo.setBalance( currentUser.getBalance() );

        }

        serviceLeaderInfo.setModeratorFio( currentUser.getModeratorFio() );

        Organization organization = organizationRepository.findOne( currentUser.getOrganizationId() );
        serviceLeaderInfo.setServiceName( organization != null ? organization.getShortName() : null );

        return ResponseEntity.ok( serviceLeaderInfo );
    }

    @GetMapping("/moderator")
    public ResponseEntity getModeratorInfo(@RequestParam(value = "organizationId", required = false) Integer organizationId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole(currentUser, "MODERATOR") &&
                !UserHelper.hasRole(currentUser, "ADMIN") )
            return ResponseEntity.notFound().build();

        ModeratorInfo moderatorInfo = new ModeratorInfo();
        moderatorInfo.setServicesCount( userRepository.countUsersByRoleName("SERVICE_LEADER") );
        moderatorInfo.setModeratorsCount( userRepository.countUsersByRoleName("MODERATOR") );

        if ( organizationId != null ) {

            User serviceLeader = userRepository.findUserByOrganizationId( organizationId );

            if ( serviceLeader != null && serviceLeader.getOrganizationId() != null && serviceLeader.getIsApproved() ) {

                moderatorInfo.setByService(true);
                Subscription subscription = serviceLeader.getCurrentSubscription();

                if ( subscription != null ) {

                    Integer documentCount =
                            documentsRepository.countDocumentsByOrganizationIdAndDates( serviceLeader.getOrganizationId(),
                                    subscription.getStartDate(), subscription.getEndDate() );
                    moderatorInfo.setDocumentsRemainsAll( Math.max( subscription.getDocumentsCount() - documentCount, 0 ) );
                    moderatorInfo.setTotalDocumentsAll( subscription.getDocumentsCount() );

                    SubscriptionType renewalType = currentUser.getSubscriptionType();

                    if ( renewalType == null )
                        moderatorInfo.setBalanceValid(
                                subscription.getIsRenewable() && serviceLeader.getBalance() - subscription.getRenewalCost() > 0
                        );
                    else
                        moderatorInfo.setBalanceValid(
                                serviceLeader.getBalance() - renewalType.getCost() > 0
                        );

                }
                else {
                    moderatorInfo.setDocumentsRemainsAll( 0 );
                    moderatorInfo.setTotalDocumentsAll( 0 );
                }

                moderatorInfo.setTotalDraftAll( documentsRepository.countByOrganizationIdAndState( serviceLeader.getOrganizationId(), 2 ) );
                moderatorInfo.setBalanceAll( serviceLeader.getBalance() );

            }

        }
        else {

            int documentsRemainsAll = 0;
            int totalDocumentsAll = 0;
            double balanceAll = 0.0;

            List<User> serviceLeaders = userRepository.findUsersByRoleName("SERVICE_LEADER");
            for (User serviceLeader : serviceLeaders) {

                if ( serviceLeader.getOrganizationId() == null || !serviceLeader.getIsApproved() )
                    continue;

                balanceAll += serviceLeader.getBalance();

                Subscription subscription = serviceLeader.getCurrentSubscription();

                if ( subscription == null )
                    continue;

                Integer documentCount =
                        documentsRepository.countDocumentsByOrganizationIdAndDates( serviceLeader.getOrganizationId(),
                                subscription.getStartDate(), subscription.getEndDate() );

                totalDocumentsAll += subscription.getDocumentsCount();
                documentsRemainsAll += subscription.getDocumentsCount() - documentCount;
            }

            moderatorInfo.setDocumentsRemainsAll( Math.max( documentsRemainsAll, 0 ) );
            moderatorInfo.setTotalDocumentsAll( totalDocumentsAll );
            moderatorInfo.setBalanceAll( balanceAll );
            moderatorInfo.setTotalDraftAll( documentsRepository.countByState( 2 ) );

        }

        return ResponseEntity.ok( moderatorInfo );
    }

    private Double calculateTotalRepairSum(User currentUser) {
        List<DocumentServiceDetail> documents = documentsRepository.findByClientIdAndState( currentUser.getClientId(), 4 );

        return documents.stream().mapToDouble( document -> {
            double totalSum = 0.0;

            try {
                totalSum += document.getDocumentOutHeader().getDocumentOut().getServiceWorks()
                        .stream()
                        .mapToDouble( serviceWork -> serviceWork.getServiceWorkTotalCost( true ) ).sum();
            }
            catch(NullPointerException ignored) {}

            try {
                totalSum += document.getDocumentOutHeader().getDocumentOut().getServiceGoodsAddons()
                        .stream()
                        .mapToDouble( serviceGoodsAddon -> serviceGoodsAddon.getServiceGoodsCost( true ) ).sum();
            }
            catch(NullPointerException ignored) {}

            return totalSum;
        } ).sum();
    }

    @GetMapping("/documents/count")
    public ResponseEntity count() {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Integer result = null;

        if ( UserHelper.hasRole(currentUser, "ADMIN") )
            result = documentsRepository.countAll();
        else if ( ( UserHelper.hasRole(currentUser, "MODERATOR") ) ) {

            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            if ( clientIds.size() == 0 )
                result = 0;
            else
                result = documentsRepository.countByClientIds( clientIds );

        }
        else if ( UserHelper.hasRole(currentUser, "CLIENT") ) {

            if ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByClientId( currentUser.getClientId() );

        }
        else if ( UserHelper.hasRole(currentUser, "SERVICE_LEADER") ) {

            if ( currentUser.getOrganizationId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByOrganizationId( currentUser.getOrganizationId() );

        }

        if ( result == null ) return ResponseEntity.ok(0);

        return ResponseEntity.ok( result );

    }

    @GetMapping("/documents/count/state")
    public ResponseEntity countByState(@RequestParam("state") Integer state) {

        User currentUser = userRepository.findCurrentUser();
        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Integer result = null;

        if ( UserHelper.hasRole(currentUser, "ADMIN") )
            result = documentsRepository.countByState(state);
        else if ( ( UserHelper.hasRole(currentUser, "MODERATOR") ) ) {

            List<Integer> clientIds = userRepository.collectClientIds( currentUser.getId() );
            if ( clientIds.size() == 0 )
                result = 0;
            else
                result = documentsRepository.countByClientIdsAndState( clientIds, state );

        }
        else if ( UserHelper.hasRole(currentUser, "CLIENT") ) {

            if ( currentUser.getClientId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByClientIdAndState( currentUser.getClientId(), state );

        }
        else if ( UserHelper.hasRole(currentUser, "SERVICE_LEADER") ) {

            if ( currentUser.getOrganizationId() == null || !currentUser.getIsApproved() ) result = null;
            else result = documentsRepository.countByOrganizationIdAndState( currentUser.getOrganizationId(), state );

        }

        if ( result == null ) return ResponseEntity.ok(0);

        return ResponseEntity.ok( result );

    }

    @GetMapping("/users/count")
    public ResponseEntity count(@RequestParam("notApprovedOnly") Boolean notApprovedOnly) {

        User currentUser = userRepository.findCurrentUser();

        if ( currentUser == null ) return ResponseEntity.status(401).build();

        Long result = null;

        if ( UserHelper.hasRole( currentUser, "ADMIN" ) ) {
            if ( notApprovedOnly )
                result = userRepository.countAllNotApproved();
            else
                result = userRepository.countAll();
        }
        else if ( UserHelper.hasRole( currentUser, "MODERATOR" ) ) {
            if ( notApprovedOnly )
                result = userRepository.countAllNotApprovedByModeratorId(currentUser.getId());
            else
                result = userRepository.countAllByModeratorId(currentUser.getId());
        }

        if ( result == null ) return ResponseEntity.ok(0);

        return ResponseEntity.ok(result);

    }
}
