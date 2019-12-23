package io.swagger.service;

import io.swagger.firebird.model.Client;
import io.swagger.firebird.model.Contact;
import io.swagger.firebird.model.DocumentServiceDetail;
import io.swagger.firebird.repository.ClientRepository;
import io.swagger.firebird.repository.ContactRepository;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.CompiledReport;
import io.swagger.postgres.model.DocumentUserState;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.model.security.UserRole;
import io.swagger.postgres.repository.CompiledReportRepository;
import io.swagger.postgres.repository.DocumentUserStateRepository;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.postgres.repository.UserRoleRepository;
import io.swagger.response.exception.DataNotFoundException;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private DocumentUserStateRepository documentUserStateRepository;
    @Autowired
    private DocumentServiceDetailRepository documentServiceDetailRepository;
    @Autowired
    private SmsService smsService;
    @Autowired
    private PasswordGenerationService passwordGenerationService;
    @Autowired
    private PasswordEncoder userPasswordEncoder;
    @Autowired
    private ReportService reportService;
    @Autowired
    private CompiledReportRepository compiledReportRepository;

    @Value("${domain.url}")
    private String domainUrl;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @Value("${compiled.catalog}")
    private String compiledReportCatalog;
    @Value("${compiled.withPrint}")
    private Boolean compiledWithPrint;
    @Value("${scheduler.disabled}")
    private Boolean isSchedulerDisabled;

    @Scheduled(fixedRate = 600000)
//    @Scheduled(fixedRate = 10000)
    public void scheduleFixedRateTask() {

        if ( isSchedulerDisabled ) return;

        logger.info(" [ SCHEDULER ] Starting schedule...");

        List<User> users = userRepository.findUsersByRoleName("CLIENT");
        List<Client> clients = clientRepository.findAll();

        for ( User user : users ) {

            if ( user.getClientId() == null || !user.getIsApproved() ) {
                logger.warn(" [ SCHEDULER ] User \"{}\" doesn't have client id or not approved...", user.getFio() );
                continue;
            }

            processUser(user);

        }

        for ( Client client : clients ) {
            processClient(client);
        }
    }

    private void processUser(User user) {
        logger.info(" [ SCHEDULER ] ------------------ ");
        logger.info(" [ SCHEDULER ] Processing user \"{}\"...", user.getFio() );

        DocumentUserState documentUserState = documentUserStateRepository.findByUserId( user.getId() );
        Integer[] documentIds = documentServiceDetailRepository.collectIdsByClientIdAndState( user.getClientId(), 4 );
        logger.info(" [ SCHEDULER ] Found documents {}", Arrays.toString( documentIds ) );

        if ( documentUserState == null ) {
            logger.info(" [ SCHEDULER ] State not found. Searching by client id...");
            documentUserState = documentUserStateRepository.findByClientId( user.getClientId() );
        }

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

                        if ( user.getPhone() != null && user.getAllowSms() ) {
                            String fio = user.getFio();
                            String smsText;
                            String uuid = compileReport( documentId );

                            if ( uuid != null ) {

                                if ( fio == null ) {
                                    smsText = String.format("По вашему автомобилю добавлен новый заказ-наряд. " +
                                            "Просмотр доступен по адресу: %s/report/?uuid=%s", domainUrl, uuid);
                                }
                                else {
                                    smsText = String.format("Уважаемый(-ая) %s, по вашему автомобилю добавлен новый заказ-наряд. " +
                                            "Просмотр доступен по адресу: %s/report/?uuid=%s", fio, domainUrl, uuid);
                                }
                                logger.info(" [ SCHEDULER ] Prepared sms text: \"{}\"", smsText );

                                smsService.sendSmsAsync( user.getPhone(), smsText );

                            }
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

    private void processClient(Client client) {
        logger.info(" [ SCHEDULER ] ------------------ ");
        logger.info(" [ SCHEDULER ] Processing client \"{}\"...", client.getShortName() );

        DocumentUserState documentUserState = documentUserStateRepository.findByClientId( client.getId() );
        Integer[] documentIds = documentServiceDetailRepository.collectIdsByClientIdAndState( client.getId(), 4 );
        logger.info(" [ SCHEDULER ] Found documents {}", Arrays.toString( documentIds ) );

        if ( documentUserState == null ) {
            logger.info(" [ SCHEDULER ] State not found. Creating new...");

            documentUserState = new DocumentUserState();
            documentUserState.setClientId( client.getId() );
            documentUserState.setDocuments( documentIds );
            documentUserState.setLastUpdateDate( new Date() );
        }
        else if ( documentUserState.getUser() != null ) {
            logger.info(" [ SCHEDULER ] User already registered, skip processing...");
        }
        else {
            logger.info(" [ SCHEDULER ] Found state documents {}", Arrays.toString( documentUserState.getDocuments() ) );

            if ( !Arrays.equals( documentIds, documentUserState.getDocuments() ) ) {
                logger.info(" [ SCHEDULER ] Found state difference!" );
                boolean userRegistered = false;

                for (Integer documentId : documentIds) {

                    if ( !contains( documentUserState.getDocuments(), documentId ) ) {

                        if ( userRegistered ) {
                            logger.info(" [ SCHEDULER ] User already registered, skip processing...");
                            break;
                        }

                        logger.info(" [ SCHEDULER ] Found new document: {}", documentId );
                        User user = buildUser( client );

                        if ( user == null ) break;

                        String password;
                        try {
                            password = passwordGenerationService.generatePassword();
                            user.setPassword( userPasswordEncoder.encode( password ) );

                            User userModerator = userService.setModerator( user );

                            userRepository.save(user);

                            if ( userModerator != null )
                                userService.buildRegistrationEventMessage( userModerator, user );
                        }
                        catch ( Exception e ) {
                            e.printStackTrace();
                            break;
                        }

                        userRegistered = true;
                        documentUserState.setUser( user );
                        logger.info(" [ SCHEDULER ] User successfully registered: {}", password );

                        if ( user.getPhone() != null && user.getAllowSms() ) {

                            String uuid = compileReport( documentId );

                            if ( uuid != null ) {

                                String smsText = String.format("По вашему автомобилю добавлен новый заказ-наряд. " +
                                        "Логин для входа в систему: ваш телефон, пароль: %s. " +
                                        "Просмотр доступен по адресу: %s/report/?uuid=%s", password, domainUrl, uuid);
                                logger.info(" [ SCHEDULER ] Prepared sms text: \"{}\"", smsText );

                                smsService.sendSmsAsync( user.getPhone(), smsText );

                            }

                        }
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

    private User buildUser(Client client) {
        Contact contact = contactRepository.findContactByClientId( client.getId() );

        if ( contact == null ) {
            logger.warn(" [ SCHEDULER ] Contact for client not found!");
            return null;
        }

        if ( !contact.getAllowSms().equals( (short) 1) ) {
            logger.warn(" [ SCHEDULER ] Contact not allow to send SMS!");
            return null;
        }

        if ( contact.getMobile() == null ) {
            logger.warn(" [ SCHEDULER ] Contact has no mobile phone!");
            return null;
        }

        String phone = userService.preparePhone( contact.getMobile() );

        if ( phone == null ) {
            logger.warn(" [ SCHEDULER ] Phone is not valid!");
            return null;
        }

        logger.info(" [ SCHEDULER ] Prepared phone: {} [ {} ]",
                phone, contact.getMobile() );

        String email = contact.getEmail();
        if ( email != null && email.length() > 0 && userRepository.isUserExistsEmail( email ) ) {
            logger.warn(" [ SCHEDULER ] User with this email is already exists!");
            return null;
        }
        if ( userRepository.isUserExistsPhone( phone ) ) {
            logger.warn(" [ SCHEDULER ] User with this phone is already exists!");
            return null;
        }
        if ( userRepository.isUserExistsClientId( client.getId() ) ) {
            logger.warn(" [ SCHEDULER ] User with this client id is already exists!");
            return null;
        }

        User user = new User();
        user.setUsername( UUID.randomUUID().toString() );
        user.setPhone(phone);
        user.setEmail(email);
        user.setClientId( client.getId() );
        user.setIsApproved(true);
        user.setAllowSms(true);
        user.setInVacation(false);
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setAccountExpired(false);
        user.setCredentialsExpired(false);
        user.setIsAutoRegistered(true);

        UserRole userRole = userRoleRepository.findByName("CLIENT");
        if ( userRole != null ) user.getRoles().add( userRole );

        return user;
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

    private String compileReport(Integer documentId) {
        try {
            byte[] reportSource = reportService.getOrderReport( documentId, compiledWithPrint );
            String uuid = UUID.randomUUID().toString();

            File reportFile = new File( String.format( "%s%s", compiledReportCatalog, uuid ) );

            FileOutputStream fos = new FileOutputStream( reportFile );
            fos.write( reportSource );
            fos.flush();
            fos.close();

            CompiledReport compiledReport = new CompiledReport();
            compiledReport.setFileName("Заказ-наряд.pdf");
            compiledReport.setCompileDate( new Date() );
            compiledReport.setUuid( uuid );

            compiledReportRepository.save( compiledReport );

            return uuid;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
