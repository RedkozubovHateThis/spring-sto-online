package io.swagger.service;

import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.postgres.model.DocumentUserState;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.DocumentUserStateRepository;
import io.swagger.postgres.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentUserStateRepository documentUserStateRepository;
    @Autowired
    private DocumentServiceDetailRepository documentServiceDetailRepository;
    @Autowired
    private SmsService smsService;

    @Value("${domain.url}")
    private String domainUrl;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @Scheduled(fixedRate = 600000)
    public void scheduleFixedRateTask() {

        if ( demoDomain ) return;

        logger.info(" [ SCHEDULER ] Starting schedule...");

        List<User> users = userRepository.findUsersByRoleName("CLIENT");

        for ( User user : users ) {

            if ( user.getClientId() == null || !user.getIsApproved() ) {
                logger.warn(" [ SCHEDULER ] User \"{}\" doesn't have client id or not approved...", user.getFio() );
                continue;
            }

            processUser(user);

        }
    }

    private void processUser(User user) {
        logger.info(" [ SCHEDULER ] ------------------ ");
        logger.info(" [ SCHEDULER ] Processing user \"{}\"...", user.getFio() );

        DocumentUserState documentUserState = documentUserStateRepository.findByUserId( user.getId() );
        Integer[] documentIds = documentServiceDetailRepository.collectIdsByClientId( user.getClientId() );
        logger.info(" [ SCHEDULER ] Found documents {}", Arrays.toString( documentIds ) );

        if ( documentUserState == null ) {
            logger.info(" [ SCHEDULER ] State not found. Creating new...");

            documentUserState = new DocumentUserState();
            documentUserState.setUser( user );
            documentUserState.setDocuments( documentIds );
            documentUserState.setLastUpdateDate( new Date() );
        }
        else {
            logger.info(" [ SCHEDULER ] Found state documents {}", Arrays.toString( documentUserState.getDocuments() ) );

            if ( !Arrays.equals( documentIds, documentUserState.getDocuments() ) ) {
                logger.info(" [ SCHEDULER ] Found state difference!" );

                for (Integer documentId : documentIds) {

                    if ( !contains( documentUserState.getDocuments(), documentId ) ) {
                        logger.info(" [ SCHEDULER ] Found new document: {}", documentId );
                        String smsText = String.format("Уважаемый(-ая) %s, по вашему автомобилю добавлен новый заказ-наряд. " +
                                "Просмотр доступен по адресу: %s/documents/%s", user.getFio(), domainUrl, documentId);
                        logger.info(" [ SCHEDULER ] Prepared sms text: \"{}\"", smsText );

                        if ( user.getPhone() != null ) {
                            smsService.sendSmsAsync( user.getPhone(), smsText );
                        }
                        else
                            logger.warn("User nas not phone set!");
                    }

                }

                documentUserState.setDocuments( documentIds );

            }
            else
                logger.info(" [ SCHEDULER ] State is actual!" );

            documentUserState.setLastUpdateDate( new Date() );
        }

        documentUserStateRepository.save( documentUserState );
    }

    private boolean contains(final Integer[] array, final Integer element) {
        boolean result = false;

        for ( Integer i : array ) {
            if ( i.equals( element ) ) {
                result = true;
                break;
            }
        }

        return result;
    }

}
