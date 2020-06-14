package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.SubscriptionTypeRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.info.AdminInfo;
import io.swagger.response.info.ClientInfo;
import io.swagger.response.info.ServiceLeaderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/infoBar")
public class InfoBarController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @GetMapping("/client")
    public ResponseEntity getClientInfo() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isClient( currentUser ) )
            return ResponseEntity.notFound().build();

        ClientInfo clientInfo = new ClientInfo();

//        clientInfo.setTotalDocuments( documentsRepository.countByClientId( currentUser.getClientId() ) );
//        clientInfo.setTotalDone( documentsRepository.countByClientIdAndState( currentUser.getClientId(), 4 ) );
//        clientInfo.setTotalDraft( documentsRepository.countByClientIdAndState( currentUser.getClientId(), 2 ) );
//        clientInfo.setTotalRepairSum( calculateTotalRepairSum( currentUser ) );
//        clientInfo.setTotalVehicles( modelRepository.countVehiclesByClientId( currentUser.getClientId() ) );
//        clientInfo.setTotalServices( organizationRepository.countServicesByClientId( currentUser.getClientId() ) );

        return ResponseEntity.ok( clientInfo );
    }

    @GetMapping("/serviceLeader")
    public ResponseEntity getServiceLeaderInfo() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeader( currentUser ) )
            return ResponseEntity.notFound().build();

        ServiceLeaderInfo serviceLeaderInfo = new ServiceLeaderInfo();

//        Subscription subscription = currentUser.getCurrentSubscription();
//
//        if ( subscription != null ) {
//
//            Integer documentsCount = documentsRepository.countDocumentsByOrganizationIdAndDates( currentUser.getOrganizationId(),
//                    subscription.getStartDate(), subscription.getEndDate() );
//            serviceLeaderInfo.setDocumentsRemains( Math.max( subscription.getDocumentsCount() - documentsCount, 0 ) );
//            serviceLeaderInfo.setTotalDocuments( subscription.getDocumentsCount() );
//            serviceLeaderInfo.setSubscribeName( subscription.getName() );
//            serviceLeaderInfo.setSubscribeEndDate( subscription.getEndDate() );
//
//            SubscriptionType renewalType = null;
//
//            if ( currentUser.getSubscriptionTypeId() != null )
//                renewalType = subscriptionTypeRepository.findOne( currentUser.getSubscriptionTypeId() );
//
//            if ( renewalType == null )
//                serviceLeaderInfo.setBalanceValid(
//                        subscription.getIsRenewable() && currentUser.getBalance() - subscription.getType().getCost() > 0
//                );
//            else
//                serviceLeaderInfo.setBalanceValid(
//                        currentUser.getBalance() - renewalType.getCost() > 0
//                );
//
//        }
//
//        serviceLeaderInfo.setBalance( currentUser.getBalance() );
//
//        serviceLeaderInfo.setModeratorFio( currentUser.getModeratorFio() );
//
//        Organization organization = organizationRepository.findOne( currentUser.getOrganizationId() );
//        serviceLeaderInfo.setServiceName( organization != null ? organization.getShortName() : null );

        return ResponseEntity.ok( serviceLeaderInfo );
    }

    @GetMapping("/admin")
    public ResponseEntity getModeratorInfo(@RequestParam(value = "organizationId", required = false) Integer organizationId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.notFound().build();

        AdminInfo adminInfo = new AdminInfo();
//        adminInfo.setServicesCount( userRepository.countUsersByRoleNames( Collections.singletonList("SERVICE_LEADER") ) );
//        adminInfo.setModeratorsCount( userRepository.countUsersByRoleName("MODERATOR") );
//
//        if ( organizationId != null ) {
//
//            User serviceLeader = userRepository.findUserByOrganizationId( organizationId );
//
//            if ( serviceLeader != null && serviceLeader.getOrganizationId() != null && serviceLeader.getIsApproved() ) {
//
//                adminInfo.setByService(true);
//                Subscription subscription = serviceLeader.getCurrentSubscription();
//
//                if ( subscription != null ) {
//
//                    Integer documentCount =
//                            documentsRepository.countDocumentsByOrganizationIdAndDates( serviceLeader.getOrganizationId(),
//                                    subscription.getStartDate(), subscription.getEndDate() );
//                    adminInfo.setDocumentsRemainsAll( Math.max( subscription.getDocumentsCount() - documentCount, 0 ) );
//                    adminInfo.setTotalDocumentsAll( subscription.getDocumentsCount() );
//
//                    SubscriptionType renewalType = null;
//
//                    if ( serviceLeader.getSubscriptionTypeId() != null )
//                        renewalType = subscriptionTypeRepository.findOne( serviceLeader.getSubscriptionTypeId() );
//
//                    if ( renewalType == null )
//                        adminInfo.setBalanceValid(
//                                subscription.getIsRenewable() && serviceLeader.getBalance() - subscription.getType().getCost() > 0
//                        );
//                    else
//                        adminInfo.setBalanceValid(
//                                serviceLeader.getBalance() - renewalType.getCost() > 0
//                        );
//
//                }
//                else {
//                    adminInfo.setDocumentsRemainsAll( 0 );
//                    adminInfo.setTotalDocumentsAll( 0 );
//                }
//
//                adminInfo.setTotalDraftAll( documentsRepository.countByOrganizationIdAndState( serviceLeader.getOrganizationId(), 2 ) );
//                adminInfo.setBalanceAll( serviceLeader.getBalance() );
//
//            }
//
//        }
//        else {
//
//            int documentsRemainsAll = 0;
//            int totalDocumentsAll = 0;
//            double balanceAll = 0.0;
//
//            List<User> serviceLeaders = userRepository.findUsersByRoleNames( Arrays.asList( "SERVICE_LEADER", "FREELANCER" ) );
//            for (User serviceLeader : serviceLeaders) {
//
//                if ( serviceLeader.getOrganizationId() == null || !serviceLeader.getIsApproved() )
//                    continue;
//
//                balanceAll += serviceLeader.getBalance();
//
//                Subscription subscription = serviceLeader.getCurrentSubscription();
//
//                if ( subscription == null )
//                    continue;
//
//                Integer documentCount =
//                        documentsRepository.countDocumentsByOrganizationIdAndDates( serviceLeader.getOrganizationId(),
//                                subscription.getStartDate(), subscription.getEndDate() );
//
//                totalDocumentsAll += subscription.getDocumentsCount();
//                documentsRemainsAll += subscription.getDocumentsCount() - documentCount;
//            }
//
//            adminInfo.setDocumentsRemainsAll( Math.max( documentsRemainsAll, 0 ) );
//            adminInfo.setTotalDocumentsAll( totalDocumentsAll );
//            adminInfo.setBalanceAll( balanceAll );
//            adminInfo.setTotalDraftAll( documentsRepository.countByState( 2 ) );
//
//        }

        return ResponseEntity.ok(adminInfo);
    }

//    private Double calculateTotalRepairSum(User currentUser) {
//        List<DocumentServiceDetail> documents = documentsRepository.findByClientIdAndState( currentUser.getClientId(), 4 );
//
//        return documents.stream().mapToDouble( document -> {
//            double totalSum = 0.0;
//
//            try {
//                totalSum += document.getDocumentOutHeader().getDocumentOut().getServiceWorks()
//                        .stream()
//                        .mapToDouble( serviceWork -> serviceWork.getServiceWorkTotalCost( true ) ).sum();
//            }
//            catch(NullPointerException ignored) {}
//
//            try {
//                totalSum += document.getDocumentOutHeader().getDocumentOut().getServiceGoodsAddons()
//                        .stream()
//                        .mapToDouble( serviceGoodsAddon -> serviceGoodsAddon.getServiceGoodsCost( true ) ).sum();
//            }
//            catch(NullPointerException ignored) {}
//
//            return totalSum;
//        } ).sum();
//    }
}
