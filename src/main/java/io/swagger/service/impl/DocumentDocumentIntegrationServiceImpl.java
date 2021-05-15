package io.swagger.service.impl;

import io.swagger.helper.DateHelper;
import io.swagger.postgres.model.*;
import io.swagger.postgres.model.enums.ServiceDocumentPaidStatus;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.*;
import io.swagger.response.integration.*;
import io.swagger.service.DocumentIntegrationService;
import io.swagger.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentDocumentIntegrationServiceImpl implements DocumentIntegrationService {

    private final static Logger logger = LoggerFactory.getLogger( DocumentIntegrationService.class );

    @Autowired
    private UserService userService;
    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleMileageRepository vehicleMileageRepository;
    @Autowired
    private ServiceWorkRepository serviceWorkRepository;
    @Autowired
    private ServiceAddonRepository serviceAddonRepository;

    @Override
    public void processIntegrationDocument(IntegrationDocument document, User user) throws IllegalArgumentException {
        checkAndSetFields(document);

        ServiceDocumentStatus status;
        ServiceDocumentPaidStatus paidStatus;
        Date startDate;
        Date endDate;

        try {
            status = ServiceDocumentStatus.valueOf( document.getStatus() );
        }
        catch(IllegalArgumentException iae) {
            throw new IllegalArgumentException( String.format("Неверный статус заказ-наряда: %s", document.getStatus()) );
        }

        try {
            paidStatus = ServiceDocumentPaidStatus.valueOf( document.getPaidStatus() );
        }
        catch(IllegalArgumentException iae) {
            throw new IllegalArgumentException( String.format("Неверный статус оплаты заказ-наряда: %s", document.getStatus()) );
        }

        try {
            startDate = DateHelper.parseDateTime( document.getStartDate() );
        }
        catch(ParseException pe) {
            throw new IllegalArgumentException( String.format("Неверный формат даты начала ремонта: %s", document.getStartDate()) );
        }

        try {
            endDate = DateHelper.parseDateTime( document.getEndDate() );
        }
        catch(ParseException pe) {
            throw new IllegalArgumentException( String.format("Неверный формат даты окончания ремонта: %s", document.getStartDate()) );
        }

        prepareDocument(document, user, status, paidStatus, startDate, endDate);
    }

    private void prepareDocument(IntegrationDocument integrationDocument, User user, ServiceDocumentStatus status,
                                 ServiceDocumentPaidStatus paidStatus, Date startDate, Date endDate) {
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Preparing document... ");

        IntegrationProfile integrationExecutor = integrationDocument.getExecutor();
        IntegrationClient integrationClient = integrationDocument.getClient();
        IntegrationProfile integrationCustomer = integrationDocument.getCustomer();

        ServiceDocument document = getServiceDocument(integrationDocument);
        Profile executor = getExecutor( integrationExecutor );
        Profile client = getClient( integrationClient, user );
        Customer customer = getCustomer( integrationCustomer, user );
        Vehicle vehicle = getVehicle( integrationDocument, user );
        VehicleMileage vehicleMileage = getVehicleMileage( document, integrationDocument, vehicle );
        List<ServiceWork> serviceWorks = getServiceWorks(document, integrationDocument.getWorks());
        List<ServiceAddon> serviceAddons = getServiceAddons(document, integrationDocument.getAddons());

        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Filling document... ");

        document.setStatus( status );
        document.setPaidStatus( paidStatus );
        document.setStartDate( startDate );
        document.setEndDate( endDate );

        if ( integrationClient != null )
            document.setClientIsCustomer( integrationClient.getClientIsCustomer() );

        document.setClient( client );
        document.setExecutor( executor );
        document.setCustomer( customer );
        document.setVehicle( vehicle );
        document.setVehicleMileage( vehicleMileage );

        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Saving document... ");

        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Document details:");
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Document number: {}", document.getNumber());
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Document integration id: {}", document.getIntegrationId());
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");

        if ( document.getExecutor() != null ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Executor: {}", document.getExecutor().getName());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Executor inn: {}", document.getExecutor().getInn());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Executor integration id: {}", document.getExecutor().getIntegrationId());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
        }

        if ( document.getClient() != null ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Client: {}", document.getClient().getName());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Client inn: {}", document.getClient().getInn());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Client integration id: {}", document.getClient().getIntegrationId());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
        }

        if ( document.getVehicle() != null ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Vehicle: {}", document.getVehicle().getModelName());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Vehicle VIN-number: {}", document.getVehicle().getVinNumber());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Vehicle integration id: {}", document.getVehicle().getIntegrationId());
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
        }

        serviceDocumentRepository.save( document );
        if ( serviceWorks != null && serviceWorks.size() > 0 ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Saving service works... ");

            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Service works details:");
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");

            for (ServiceWork serviceWork : serviceWorks) {
                logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Service work: {}", serviceWork.getName());
                logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Service work integration id: {}", serviceWork.getIntegrationId());
                logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
            }

            serviceWorkRepository.saveAll( serviceWorks );
        }
        if ( serviceAddons != null && serviceAddons.size() > 0 ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Saving service addons... ");

            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Service addons details:");
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");

            for (ServiceAddon serviceAddon : serviceAddons) {
                logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Service addon: {}", serviceAddon.getName());
                logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Service addon integration id: {}", serviceAddon.getIntegrationId());
                logger.info(" [ DOCUMENT INTEGRATION SERVICE ] ");
            }

            serviceAddonRepository.saveAll( serviceAddons );
        }

        if ( client != null ) {
            try {
                logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Updating client... ");
                if ( client.getUser() == null )
                    client.setByFio(true);
                userService.updateUser( client, user );
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ServiceDocument getServiceDocument(IntegrationDocument integrationDocument) {
        ServiceDocument document = serviceDocumentRepository.findByIntegrationId( integrationDocument.getIntegrationId() );

        if ( document == null ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Document not found, creating new one... ");
            document = new ServiceDocument();
            document.setDeleted( false );
            document.setIntegrationId( integrationDocument.getIntegrationId() );
        }

        if ( serviceDocumentRepository.isExistsByNumberNotSelf(
                integrationDocument.getNumber()
        ) ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Document with this number exists: {}, replacing number...", integrationDocument.getNumber());
            document.setNumber( integrationDocument.getNumber() + "И" );
        }
        else
            document.setNumber( integrationDocument.getNumber() );
        document.setCost( integrationDocument.getCost() );
        document.setReason( integrationDocument.getReason() );
        document.setMasterFio( integrationDocument.getMasterFio() );

        return document;
    }

    private Profile getExecutor(IntegrationProfile integrationExecutor) throws IllegalArgumentException {
        if ( integrationExecutor == null ) return null;
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Searching for executor... ");

        Profile executor = profileRepository.findOneByIntegrationId( integrationExecutor.getIntegrationId() );

        if ( executor == null && !isFieldEmpty( integrationExecutor.getInn() ) )
            executor = profileRepository.findOneByInn( integrationExecutor.getInn() );

        if ( executor == null && !isFieldEmpty( integrationExecutor.getPhone() ) )
            executor = profileRepository.findOneByPhone( integrationExecutor.getPhone() );

        if ( executor == null )
            throw new IllegalArgumentException( String.format("Исполнитель с данным ИНН/телефоном не найден: %s/%s", integrationExecutor.getInn(), integrationExecutor.getPhone()) );

//        executor.setInn( integrationExecutor.getInn() );
        executor.setName( integrationExecutor.getName() );
        executor.setAddress( integrationExecutor.getAddress() );
//        executor.setPhone( integrationExecutor.getPhone() );
//        executor.setEmail( integrationExecutor.getEmail() );
        executor.setIntegrationId( integrationExecutor.getIntegrationId() );

        profileRepository.save(executor);

        return executor;
    }

    private Profile getClient(IntegrationClient integrationClient, User user) throws IllegalArgumentException {
        if ( integrationClient == null ) return null;
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Searching for client... ");

        Profile client = profileRepository.findOneByIntegrationId( integrationClient.getIntegrationId() );

        if ( client == null )
            client = profileRepository.findOneByPhone( integrationClient.getPhone() );

        if ( client == null ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Client not found, creating new one... ");
            client = new Profile();
            client.setDeleted( false );
            client.setAutoRegister( true );
            client.setByFio( true );
            client.setPhone( integrationClient.getPhone() );
            client.setEmail( integrationClient.getEmail() );

            if ( user.getProfile() != null )
                client.setCreatedBy( user.getProfile() );

            if ( !isFieldEmpty( client.getEmail() ) && profileRepository.isProfileExistsEmail( client.getEmail() ) )
                throw new IllegalArgumentException(
                        String.format( "Данная почта уже указана у другого клиента: %s", client.getEmail() )
                );
        }

        client.setIntegrationId( integrationClient.getIntegrationId() );
        client.setInn( integrationClient.getInn() );
        client.setName( integrationClient.getName() );
        client.setAddress( integrationClient.getAddress() );
        client.setFirstName( integrationClient.getFirstName() );
        client.setLastName( integrationClient.getLastName() );
        client.setMiddleName( integrationClient.getMiddleName() );

        profileRepository.save(client);

        return client;
    }

    private Customer getCustomer(IntegrationProfile integrationCustomer, User user) throws IllegalArgumentException {
        if ( integrationCustomer == null ) return null;
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Searching for customer... ");

        Customer customer = customerRepository.findOneByIntegrationId( integrationCustomer.getIntegrationId() );

        if ( customer == null )
            customer = customerRepository.findOneByInn( integrationCustomer.getInn() );

        if ( customer == null ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Customer not found, creating new one... ");
            customer = new Customer();
            customer.setDeleted( false );

            if ( user.getProfile() != null )
                customer.setCreatedBy( user.getProfile() );
        }

        customer.setIntegrationId( integrationCustomer.getIntegrationId() );
        customer.setInn( integrationCustomer.getInn() );
        customer.setName( integrationCustomer.getName() );
        customer.setPhone( integrationCustomer.getPhone() );
        customer.setEmail( integrationCustomer.getEmail() );
        customer.setAddress( integrationCustomer.getAddress() );

        customerRepository.save(customer);

        return customer;
    }

    private Vehicle getVehicle(IntegrationDocument integrationDocument, User user) {
        if ( isFieldEmpty( integrationDocument.getVinNumber() ) && isFieldEmpty( integrationDocument.getVehicleIntegrationId() ) )
            return null;
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Searching for vehicle... ");

        Vehicle vehicle = null;

        if ( !isFieldEmpty( integrationDocument.getVehicleIntegrationId() ) )
            vehicle = vehicleRepository.findOneByIntegrationId( integrationDocument.getVehicleIntegrationId() );
        if ( vehicle == null && !isFieldEmpty( integrationDocument.getVinNumber() ) )
            vehicle = vehicleRepository.findOneByVinNumber( integrationDocument.getVinNumber() );

        if ( vehicle == null ) {
            logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Vehicle not found, creating new one... ");
            vehicle = new Vehicle();
            vehicle.setDeleted( false );

            if ( user.getProfile() != null )
                vehicle.setCreatedBy( user.getProfile() );
        }

        vehicle.setIntegrationId( integrationDocument.getVehicleIntegrationId() );
        vehicle.setModelName( integrationDocument.getModelName() );
        vehicle.setVinNumber( prepareVinNumber( integrationDocument.getVinNumber() ) );
        vehicle.setRegNumber( prepareRegNumber( integrationDocument.getRegNumber() ) );
        vehicle.setYear( integrationDocument.getYear() );

        vehicleRepository.save( vehicle );

        return vehicle;
    }

    private VehicleMileage getVehicleMileage(ServiceDocument document, IntegrationDocument integrationDocument,
                                             Vehicle vehicle) {
        if ( integrationDocument.getMileage() == null || vehicle == null )
            return null;
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Searching for vehicle mileage... ");

        if ( document.getId() == null )
            return buildVehicleMileage(integrationDocument, new VehicleMileage(), vehicle);

        VehicleMileage vehicleMileage = vehicleMileageRepository.findOneByDocumentId( document.getId() );

        if ( vehicleMileage == null )
            return buildVehicleMileage(integrationDocument, new VehicleMileage(), vehicle);
        else
            return buildVehicleMileage(integrationDocument, vehicleMileage, vehicle);
    }

    private List<ServiceWork> getServiceWorks(ServiceDocument serviceDocument, List<IntegrationWork> integrationWorks) {
        if ( integrationWorks == null || integrationWorks.size() == 0 ) return null;
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Searching for service works... ");

        List<ServiceWork> serviceWorks;

        if ( serviceDocument.getId() != null ) {
            serviceWorks = serviceWorkRepository.findByDocumentId( serviceDocument.getId() );

            if ( serviceWorks.size() > 0 ) {
                List<String> integrationIds = integrationWorks
                        .stream()
                        .map( IntegrationWork::getIntegrationId )
                        .collect( Collectors.toList() );

                for (ServiceWork serviceWork : serviceWorks) {
                    if ( isFieldEmpty( serviceWork.getIntegrationId() )
                    || !integrationIds.contains( serviceWork.getIntegrationId() ) )
                        serviceWork.setDeleted( true );
                }
            }
        }
        else
            serviceWorks = new ArrayList<>();

        for (IntegrationWork integrationWork : integrationWorks) {
            Optional<ServiceWork> serviceWorkOptional = serviceWorks
                    .stream()
                    .filter( serviceWork -> serviceWork.getIntegrationId() != null
                            && serviceWork.getIntegrationId().equals( integrationWork.getIntegrationId() ) )
                    .findFirst();

            ServiceWork serviceWork;
            if ( serviceWorkOptional.isPresent() )
                serviceWork = serviceWorkOptional.get();
            else {
                serviceWork = new ServiceWork();
                serviceWork.setDeleted( false );
                serviceWork.setDocument( serviceDocument );
                serviceWorks.add( serviceWork );
            }

            serviceWork.setIntegrationId( integrationWork.getIntegrationId() );
            serviceWork.setByPrice( integrationWork.getByPrice() );
            serviceWork.setCount( integrationWork.getCount() );
            serviceWork.setName( integrationWork.getName() );
            serviceWork.setNumber( integrationWork.getNumber() );
            serviceWork.setPrice( integrationWork.getPrice() );
            serviceWork.setPriceNorm( integrationWork.getPriceNorm() );
            serviceWork.setTimeValue( integrationWork.getTimeValue() );
        }

        return serviceWorks;
    }

    private List<ServiceAddon> getServiceAddons(ServiceDocument serviceDocument, List<IntegrationAddon> integrationAddons) {
        if ( integrationAddons == null || integrationAddons.size() == 0 ) return null;
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Searching for service addons... ");

        List<ServiceAddon> serviceAddons;

        if ( serviceDocument.getId() != null ) {
            serviceAddons = serviceAddonRepository.findByDocumentId( serviceDocument.getId() );

            if ( serviceAddons.size() > 0 ) {
                List<String> integrationIds = integrationAddons
                        .stream()
                        .map( IntegrationAddon::getIntegrationId )
                        .collect( Collectors.toList() );

                for (ServiceAddon serviceAddon : serviceAddons) {
                    if ( isFieldEmpty( serviceAddon.getIntegrationId() )
                            || !integrationIds.contains( serviceAddon.getIntegrationId() ) )
                        serviceAddon.setDeleted( true );
                }
            }
        }
        else
            serviceAddons = new ArrayList<>();

        for (IntegrationAddon integrationAddon : integrationAddons) {
            Optional<ServiceAddon> serviceWorkOptional = serviceAddons
                    .stream()
                    .filter( serviceAddon -> serviceAddon.getIntegrationId() != null
                            && serviceAddon.getIntegrationId().equals( integrationAddon.getIntegrationId() ) )
                    .findFirst();

            ServiceAddon serviceAddon;
            if ( serviceWorkOptional.isPresent() )
                serviceAddon = serviceWorkOptional.get();
            else {
                serviceAddon = new ServiceAddon();
                serviceAddon.setDeleted( false );
                serviceAddon.setDocument( serviceDocument );
                serviceAddons.add( serviceAddon );
            }

            serviceAddon.setIntegrationId( integrationAddon.getIntegrationId() );
            serviceAddon.setCount( integrationAddon.getCount() );
            serviceAddon.setName( integrationAddon.getName() );
            serviceAddon.setNumber( integrationAddon.getNumber() );
            serviceAddon.setCost( integrationAddon.getCost() );
        }

        return serviceAddons;
    }

    private VehicleMileage buildVehicleMileage(IntegrationDocument integrationDocument, VehicleMileage vehicleMileage,
                                               Vehicle vehicle) {
        if ( vehicleMileage.getId() == null )
            vehicleMileage.setDeleted( false );

        vehicleMileage.setMileage( integrationDocument.getMileage() );
        vehicleMileage.setVehicle( vehicle );

        vehicleMileageRepository.save( vehicleMileage );

        return vehicleMileage;
    }

    private void checkAndSetFields(IntegrationDocument document) throws IllegalArgumentException {
        logger.info(" [ DOCUMENT INTEGRATION SERVICE ] Checking and settings fields... ");

        if ( isFieldEmpty( document.getIntegrationId() ) )
            throw new IllegalArgumentException("Не указан идентификатор заказ-наряда");
        if ( isFieldEmpty( document.getStatus() ) )
            document.setStatus( ServiceDocumentStatus.CREATED.name() );
        if ( isFieldEmpty( document.getPaidStatus() ) )
            document.setPaidStatus( ServiceDocumentPaidStatus.NOT_PAID.name() );
        if ( isFieldEmpty( document.getNumber() ) )
            throw new IllegalArgumentException("Не указан номер заказ-наряда");
        if ( isFieldEmpty( document.getStartDate() ) )
            throw new IllegalArgumentException("Не указана дата начала ремонта");

        if ( document.getExecutor() != null ) {
            IntegrationProfile executor = document.getExecutor();

            if ( isFieldEmpty( executor.getIntegrationId() ) )
                throw new IllegalArgumentException("Не указан идентификатор исполнителя");
            if ( isFieldEmpty( executor.getInn() ) && isFieldEmpty( executor.getPhone() ) )
                throw new IllegalArgumentException("Не указан ИНН или телефон исполнителя");

            if ( !isFieldEmpty( executor.getPhone() ) ) {
                String oldPhone = executor.getPhone();
                executor.setPhone( userService.processPhone( executor.getPhone() ) );
                if ( !userService.isPhoneValid( executor.getPhone() ) )
                    throw new IllegalArgumentException( String.format("Неверный формат телефона исполнителя: %s", oldPhone) );
            }
            if ( !isFieldEmpty( executor.getEmail() ) && !userService.isEmailValid( executor.getEmail() ) )
                throw new IllegalArgumentException( String.format("Неверный формат email исполнителя: %s", executor.getEmail()) );
        }
        if ( document.getClient() != null ) {
            IntegrationClient client = document.getClient();

            if ( isFieldEmpty( client.getIntegrationId() ) )
                throw new IllegalArgumentException("Не указан идентификатор клиента");
            if ( isFieldEmpty( client.getPhone() ) )
                throw new IllegalArgumentException("Не указан телефон клиента");

            String oldPhone = client.getPhone();
            client.setPhone( userService.processPhone( client.getPhone() ) );
            if ( !userService.isPhoneValid( client.getPhone() ) )
                throw new IllegalArgumentException( String.format("Неверный формат телефона клиента: %s", oldPhone) );
            if ( client.getClientIsCustomer() == null )
                throw new IllegalArgumentException("Не указан признак того, является ли клиент заказчиком");
            if ( !isFieldEmpty( client.getEmail() ) && !userService.isEmailValid( client.getEmail() ) )
                throw new IllegalArgumentException( String.format("Неверный формат email клиента: %s", client.getEmail()) );
            if ( client.getClientIsCustomer() != null && !client.getClientIsCustomer() && document.getCustomer() == null )
                throw new IllegalArgumentException("Не указан заказчик");
        }
        if ( document.getCustomer() != null ) {
            IntegrationProfile customer = document.getCustomer();

            if ( isFieldEmpty( customer.getIntegrationId() ) )
                throw new IllegalArgumentException("Не указан идентификатор заказчика");
            if ( isFieldEmpty( customer.getInn() ) )
                throw new IllegalArgumentException("Не указан ИНН заказчика");

            if ( !isFieldEmpty( customer.getPhone() ) ) {
                String oldPhone = customer.getPhone();
                customer.setPhone( userService.processPhone( customer.getPhone() ) );
                if ( !userService.isPhoneValid( customer.getPhone() ) )
                    throw new IllegalArgumentException( String.format("Неверный формат телефона заказчика: %s", oldPhone) );
            }
            if ( !isFieldEmpty( customer.getEmail() ) && !userService.isEmailValid( customer.getEmail() ) )
                throw new IllegalArgumentException( String.format("Неверный формат email заказчика: %s", customer.getEmail()) );
        }
        if ( document.getWorks() != null ) {
            for (IntegrationWork work : document.getWorks()) {
                if ( isFieldEmpty( work.getIntegrationId() ) )
                    throw new IllegalArgumentException( String.format("Не указан идентификатор работы: %s", document.getWorks().indexOf( work ) + 1 ) );
                if ( work.getByPrice() == null )
                    throw new IllegalArgumentException( String.format("Не указан признак фиксированной цены работы: %s", work.getIntegrationId() ) );
                if ( !work.getByPrice() && !isFieldEmpty( work.getPrice() ) && isFieldEmpty( work.getPriceNorm() ) && isFieldEmpty( work.getTimeValue() ) )
                    throw new IllegalArgumentException( String.format("Не указан признак фиксированной цены при наличии фиксированной цены работы: %s", work.getIntegrationId() ) );
                if ( work.getCount() == null || work.getCount() == 0 )
                    work.setCount(1);
            }
        }
        if ( document.getAddons() != null ) {
            for (IntegrationAddon addon : document.getAddons()) {
                if ( isFieldEmpty(addon.getIntegrationId()) )
                    throw new IllegalArgumentException( String.format("Не указан идентификатор товара: %s", document.getAddons().indexOf( addon ) + 1 ) );
                if ( addon.getCount() == null || addon.getCount() == 0 )
                    addon.setCount(1);
            }
        }
    }

    private Boolean isFieldEmpty(String field) {
        return field == null || field.length() == 0;
    }

    private Boolean isFieldEmpty(Double field) {
        return field == null;
    }

    // TODO: вынести в хэлпер или сервис
    public String prepareVinNumber(String vinNumber) {
        if ( vinNumber == null || vinNumber.length() == 0 ) return vinNumber;

        vinNumber = vinNumber.trim();
        vinNumber = vinNumber.toUpperCase();
        vinNumber = vinNumber.replaceAll(" ", "");

        return vinNumber;
    }

    public String prepareRegNumber(String regNumber) {
        if ( regNumber == null || regNumber.length() == 0 ) return regNumber;

        regNumber = regNumber.trim();
        regNumber = regNumber.toUpperCase();
        regNumber = regNumber.replaceAll(" ", "");

        return regNumber;
    }

}
