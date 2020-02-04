package io.swagger.controller;

import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.model.Organization;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.firebird.repository.ModelDetailRepository;
import io.swagger.firebird.repository.ModelRepository;
import io.swagger.firebird.repository.OrganizationRepository;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.SubscriptionTypeRepository;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/secured/infoBar")
public class InfoBarController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentServiceDetailRepository documentsRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @GetMapping("/client")
    public ResponseEntity getClientInfo() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isClient( currentUser ) ||
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
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) )
            return ResponseEntity.notFound().build();

        if ( UserHelper.isServiceLeader( currentUser ) && !currentUser.isServiceLeaderValid() )
            return ResponseEntity.notFound().build();
        else if ( UserHelper.isFreelancer( currentUser ) && !currentUser.isFreelancerValid() )
            return ResponseEntity.notFound().build();

        ServiceLeaderInfo serviceLeaderInfo = new ServiceLeaderInfo();

        Subscription subscription = currentUser.getCurrentSubscription();

        if ( subscription != null ) {

            Integer documentsCount = null;

            if ( UserHelper.isServiceLeader( currentUser ) )
                    documentsCount = documentsRepository.countDocumentsByOrganizationIdAndDates( currentUser.getOrganizationId(),
                            subscription.getStartDate(), subscription.getEndDate() );
            else if ( UserHelper.isFreelancer( currentUser ) )
                    documentsCount = documentsRepository.countDocumentsByOrganizationIdAndDatesAndManagerId( currentUser.getOrganizationId(),
                            subscription.getStartDate(), subscription.getEndDate(), currentUser.getManagerId() );

            serviceLeaderInfo.setDocumentsRemains( Math.max( subscription.getDocumentsCount() - documentsCount, 0 ) );
            serviceLeaderInfo.setTotalDocuments( subscription.getDocumentsCount() );
            serviceLeaderInfo.setSubscribeName( subscription.getName() );
            serviceLeaderInfo.setSubscribeEndDate( subscription.getEndDate() );

            SubscriptionType renewalType = null;

            if ( currentUser.getSubscriptionTypeId() != null )
                renewalType = subscriptionTypeRepository.findOne( currentUser.getSubscriptionTypeId() );

            if ( renewalType == null )
                serviceLeaderInfo.setBalanceValid(
                        subscription.getIsRenewable() && currentUser.getBalance() - subscription.getType().getCost() > 0
                );
            else
                serviceLeaderInfo.setBalanceValid(
                        currentUser.getBalance() - renewalType.getCost() > 0
                );

        }

        serviceLeaderInfo.setBalance( currentUser.getBalance() );

        serviceLeaderInfo.setModeratorFio( currentUser.getModeratorFio() );

        Organization organization = organizationRepository.findOne( currentUser.getOrganizationId() );
        serviceLeaderInfo.setServiceName( organization != null ? organization.getShortName() : null );

        return ResponseEntity.ok( serviceLeaderInfo );
    }

    @GetMapping("/moderator")
    public ResponseEntity getModeratorInfo(@RequestParam(value = "organizationId", required = false) Integer organizationId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isAdminOrModerator( currentUser ) )
            return ResponseEntity.notFound().build();

        ModeratorInfo moderatorInfo = new ModeratorInfo();
        moderatorInfo.setServicesCount( userRepository.countUsersByRoleNames( Arrays.asList( "SERVICE_LEADER", "FREELANCER" ) ) );
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

                    SubscriptionType renewalType = null;

                    if ( serviceLeader.getSubscriptionTypeId() != null )
                        renewalType = subscriptionTypeRepository.findOne( serviceLeader.getSubscriptionTypeId() );

                    if ( renewalType == null )
                        moderatorInfo.setBalanceValid(
                                subscription.getIsRenewable() && serviceLeader.getBalance() - subscription.getType().getCost() > 0
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

            List<User> serviceLeaders = userRepository.findUsersByRoleNames( Arrays.asList( "SERVICE_LEADER", "FREELANCER" ) );
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
}
