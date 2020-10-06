package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.postgres.repository.SubscriptionTypeRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.info.ClientInfo;
import io.swagger.response.info.ServiceLeaderInfo;
import io.swagger.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/infoBar")
public class InfoBarController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @GetMapping("/client")
    public ResponseEntity getClientInfo() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isClient( currentUser ) || currentUser.getProfile() == null )
            return ResponseEntity.notFound().build();

        ClientInfo clientInfo = new ClientInfo();

        clientInfo.setTotalDocuments( serviceDocumentRepository.countByClientId( currentUser.getProfile().getId() ) );
        clientInfo.setTotalSum( serviceDocumentRepository.countTotalSumByClientId( currentUser.getProfile().getId() ) );
        clientInfo.setTotalVehicles( serviceDocumentRepository.countVehiclesByClientId( currentUser.getProfile().getId() ) );
        clientInfo.setTotalServices( serviceDocumentRepository.countServicesByClientId( currentUser.getProfile().getId() ) );

        return ResponseEntity.ok( clientInfo );
    }

    @GetMapping("/serviceLeader")
    public ResponseEntity getServiceLeaderInfo() {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isServiceLeaderOrFreelancer( currentUser ) || currentUser.getProfile() == null )
            return ResponseEntity.notFound().build();

        ServiceLeaderInfo serviceLeaderInfo = buildInfo( currentUser, currentUser.getProfile().getId(), true );

        return ResponseEntity.ok( serviceLeaderInfo );
    }

    @GetMapping("/admin")
    public ResponseEntity getAdminInfo(@RequestParam(value = "organizationId", required = false) Long organizationId) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.isAdmin( currentUser ) )
            return ResponseEntity.notFound().build();

        ServiceLeaderInfo serviceLeaderInfo = buildInfo(currentUser, organizationId, false);

        return ResponseEntity.ok(serviceLeaderInfo);
    }

    private ServiceLeaderInfo buildInfo(User currentUser, Long organizationId, Boolean byServiceLeader) {
        ServiceLeaderInfo serviceLeaderInfo = new ServiceLeaderInfo();

        Long totalDocuments = 0L;
        Long totalDocumentsCreated = 0L;
        Long totalDocumentsInWork = 0L;
        Long totalDocumentsCompleted = 0L;

        Double totalSum = 0.0;
        Long totalVehicles = 0L;
        Long totalClients = 0L;

        Double totalBalance = 0.0;
        Date adSubscriptionEndDate = null;
        Boolean adSubscriptionAvailable = null;
        Long operatorSubscriptionEndDate = null;
        Boolean operatorSubscriptionAvailable = null;
        Integer averageAdView = 0;
        Integer adEfficiency = 0;

        Double balance;
        if ( byServiceLeader ) {
            balance = currentUser.getBalance();
            Subscription adSubscription = currentUser.getCurrentAdSubscription();
            Subscription operatorSubscription = currentUser.getCurrentOperatorSubscription();

            if ( adSubscription != null ) {
                adSubscriptionEndDate = adSubscription.getEndDate();
                if ( adSubscription.getType() != null && adSubscription.getIsRenewable() )
                    adSubscriptionAvailable = ( currentUser.getBalance() - adSubscription.getType().getCost() ) > 0;
                else
                    adSubscriptionAvailable = false;
            }
            if ( operatorSubscription != null ) {

                if ( currentUser.getProfile() != null && operatorSubscription.getType() != null )
                    operatorSubscriptionEndDate = paymentService.getRemainsDocuments( currentUser.getProfile(), operatorSubscription, operatorSubscription.getType() );

                if ( operatorSubscription.getType() != null && operatorSubscription.getIsRenewable() ) {
                    if ( adSubscription != null && adSubscription.getType() != null && adSubscription.getIsRenewable() )
                        operatorSubscriptionAvailable = ( currentUser.getBalance() - adSubscription.getType().getCost() - operatorSubscription.getType().getCost() ) > 0;
                    else
                        operatorSubscriptionAvailable = ( currentUser.getBalance() - operatorSubscription.getType().getCost() ) > 0;
                }
                else
                    operatorSubscriptionAvailable = false;
            }
        }
        else {
            if ( organizationId != null ) {
                Profile profile = profileRepository.findById( organizationId ).orElse(null);

                if ( profile != null && profile.getUser() != null ) {
                    User serviceLeader = profile.getUser();
                    balance = serviceLeader.getBalance();
                    Subscription adSubscription = serviceLeader.getCurrentAdSubscription();
                    Subscription operatorSubscription = serviceLeader.getCurrentOperatorSubscription();

                    if ( adSubscription != null ) {
                        adSubscriptionEndDate = adSubscription.getEndDate();
                        if ( adSubscription.getType() != null && adSubscription.getIsRenewable() )
                            adSubscriptionAvailable = ( serviceLeader.getBalance() - adSubscription.getType().getCost() ) > 0;
                        else
                            adSubscriptionAvailable = false;
                    }
                    if ( operatorSubscription != null ) {

                        if ( operatorSubscription.getType() != null ) {
                            operatorSubscriptionEndDate = paymentService.getRemainsDocuments( profile, operatorSubscription, operatorSubscription.getType() );
                        }

                        if ( operatorSubscription.getType() != null && operatorSubscription.getIsRenewable() ) {
                            if ( adSubscription != null && adSubscription.getType() != null && adSubscription.getIsRenewable() )
                                operatorSubscriptionAvailable = ( serviceLeader.getBalance() - adSubscription.getType().getCost() - operatorSubscription.getType().getCost() ) > 0;
                            else
                                operatorSubscriptionAvailable = ( serviceLeader.getBalance() - operatorSubscription.getType().getCost() ) > 0;
                        }
                        else
                            operatorSubscriptionAvailable = false;
                    }
                }
                else
                    balance = 0.0;
            }
            else
                balance = userRepository.countTotalBalance();
        }

        totalBalance += getDouble( balance );

        if ( organizationId != null ) {
            totalDocuments += getLong( serviceDocumentRepository.countByExecutorId( organizationId ) );
            totalDocumentsCompleted += getLong( serviceDocumentRepository.countByExecutorIdAndStatus( organizationId, ServiceDocumentStatus.COMPLETED.toString() ) );
            totalDocumentsCreated += getLong( serviceDocumentRepository.countByExecutorIdAndStatus( organizationId, ServiceDocumentStatus.CREATED.toString() ) );
            totalDocumentsInWork += getLong( serviceDocumentRepository.countByExecutorIdAndStatus( organizationId, ServiceDocumentStatus.IN_WORK.toString() ) );
            totalSum += getDouble( serviceDocumentRepository.countTotalSumByExecutorId( organizationId ) );
            totalVehicles += getLong( serviceDocumentRepository.countVehiclesByExecutorId( organizationId ) );
            totalClients += getLong( serviceDocumentRepository.countClientsByExecutorId( organizationId ) );
        }
        else {
            totalDocuments += getLong( serviceDocumentRepository.count() );
            totalDocumentsCompleted += getLong( serviceDocumentRepository.countByStatus( ServiceDocumentStatus.COMPLETED.toString() ) );
            totalDocumentsCreated += getLong( serviceDocumentRepository.countByStatus( ServiceDocumentStatus.CREATED.toString() ) );
            totalDocumentsInWork += getLong( serviceDocumentRepository.countByStatus( ServiceDocumentStatus.IN_WORK.toString() ) );
            totalSum += getDouble( serviceDocumentRepository.countTotalSum() );
            totalVehicles += getLong( serviceDocumentRepository.countVehicles() );
            totalClients += getLong( serviceDocumentRepository.countClients() );
        }

        serviceLeaderInfo.setTotalDocuments( totalDocuments );
        serviceLeaderInfo.setTotalDocumentsCompleted( totalDocumentsCompleted );
        serviceLeaderInfo.setTotalDocumentsCreated( totalDocumentsCreated );
        serviceLeaderInfo.setTotalDocumentsInWork( totalDocumentsInWork );

        serviceLeaderInfo.setTotalSum( totalSum );
        serviceLeaderInfo.setTotalVehicles( totalVehicles );
        serviceLeaderInfo.setTotalClients( totalClients );

        serviceLeaderInfo.setTotalBalance( totalBalance );
        serviceLeaderInfo.setAdSubscriptionEndDate( adSubscriptionEndDate );
        serviceLeaderInfo.setAdSubscriptionAvailable( adSubscriptionAvailable );
        serviceLeaderInfo.setOperatorSubscriptionEndDate( operatorSubscriptionEndDate );
        serviceLeaderInfo.setOperatorSubscriptionAvailable( operatorSubscriptionAvailable );

        serviceLeaderInfo.setAverageAdView( averageAdView );
        serviceLeaderInfo.setAdEfficiency( adEfficiency );

        return serviceLeaderInfo;
    }

    private Long getLong(Long value) {
        if ( value == null )
            return 0L;

        return value;
    }

    private Double getDouble(Double value) {
        if ( value == null )
            return 0.0;

        return value;
    }
}
