package io.swagger.service.impl;

import io.swagger.helper.DateHelper;
import io.swagger.helper.fwMoney;
import io.swagger.postgres.model.*;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.ProfileRepository;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.ClientDocumentResponse;
import io.swagger.response.report.ClientRegisteredResponse;
import io.swagger.response.report.ClientResponse;
import io.swagger.response.report.ExecutorResponse;
import io.swagger.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final static Logger logger = LoggerFactory.getLogger( ReportService.class );

    private final String[] EXECUTORS = new String[] {"Иванов И.И", "Петров П.П", "Сидоров С.С"};
    private final String[] CLIENTS = new String[] {"ООО \"Вектор\"", "Савельев С.П", "Богатов П.В"};

    @Autowired
    private ServiceDocumentRepository serviceDocumentRepository;
    @Autowired
    private ProfileRepository profileRepository;

    @Value("${reports.catalog}")
    private String reportsCatalog;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    /* ------------------------------- DOCUMENTS REPORTS ------------------------------- */

    @Override
    public byte[] getOrderReport(Long documentId, String orderName) throws IOException, JRException, DataNotFoundException {

        ServiceDocument document = serviceDocumentRepository.findById( documentId ).orElse(null);
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + orderName );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderReportParameters( parameters, document );
        JRBeanCollectionDataSource serviceData = new JRBeanCollectionDataSource( getServiceWorkData( parameters, document ) );
        parameters.put("serviceData", serviceData);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    @Override
    public byte[] getOrderPaymentReport(Long documentId) throws IOException, JRException, DataNotFoundException {

        ServiceDocument document = serviceDocumentRepository.findById( documentId ).orElse(null);
        if ( document == null ) throw new DataNotFoundException();

        String orderName = "orderPayment.jasper";

        File template = new File( reportsCatalog + orderName );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderPaymentReportParameters( parameters, document );
        JRBeanCollectionDataSource serviceData = new JRBeanCollectionDataSource( getServiceWorkData( parameters, document ) );
        parameters.put("serviceData", serviceData);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderReportParameters(Map<String, Object> parameters, ServiceDocument document) throws DataNotFoundException {

        Vehicle vehicle = document.getVehicle();
        if ( vehicle == null ) throw new DataNotFoundException();

        VehicleMileage vehicleMileage = document.getVehicleMileage();
//        if ( vehicleMileage == null ) throw new DataNotFoundException();

        Profile executioner = document.getExecutor();
        if ( executioner == null ) throw new DataNotFoundException();

        Profile client = document.getClient();
        if ( client == null ) throw new DataNotFoundException();

        Customer customer = document.getCustomer();
        if ( !document.getClientIsCustomer() && customer == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, executioner, document);
        fillOrderParameters(parameters, document);
        fillCustomerParameters(parameters, client, customer, document.getClientIsCustomer());
        fillVehicleParameters(parameters, vehicle, vehicleMileage);
        fillRepairParameters(parameters, document);

    }

    private void fillOrderPaymentReportParameters(Map<String, Object> parameters, ServiceDocument document) throws DataNotFoundException {

        Profile executioner = document.getExecutor();
        if ( executioner == null ) throw new DataNotFoundException();

        Profile client = document.getClient();
        if ( client == null ) throw new DataNotFoundException();

        Customer customer = document.getCustomer();
        if ( !document.getClientIsCustomer() && customer == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, executioner, document);
        fillBankParameters(parameters, executioner, document);
        fillOrderParameters(parameters, document);
        fillCustomerParameters(parameters, client, customer, document.getClientIsCustomer());

    }

    private void fillOrganizationParameters(Map<String, Object> parameters, Profile executioner, ServiceDocument document) throws DataNotFoundException {
        parameters.put( "organizationName", getFieldText(executioner.getName(), "не указан") );
        parameters.put( "organizationInn", getFieldText(executioner.getInn(), "не указан") );
        parameters.put( "organizationAddress", getFieldText(executioner.getAddress(), "не указан") );
        parameters.put( "organizationPhone", getFieldText(executioner.getPhone(), "не указан") );
        parameters.put( "organizationEmail", getFieldText(executioner.getEmail(), "не указан") );
        parameters.put( "executionerFio", document.getMasterFio() );
    }

    private void fillBankParameters(Map<String, Object> parameters, Profile executioner, ServiceDocument document) throws DataNotFoundException {
        if ( executioner.getUser() == null )
            throw new DataNotFoundException();

        User executionerUser = executioner.getUser();

        parameters.put( "bankName", getFieldText(executionerUser.getBankName(), "не указан") );
        parameters.put( "bankBic", getFieldText(executionerUser.getBankBic(), "не указан") );
        parameters.put( "checkingAccount", getFieldText(executionerUser.getCheckingAccount(), "не указан") );
        parameters.put( "corrAccount", getFieldText(executionerUser.getCorrAccount(), "не указан") );
    }

    private void fillOrderParameters(Map<String, Object> parameters, ServiceDocument document) {
        parameters.put( "orderNum", document.getNumber() );
        parameters.put( "orderStartDate", DateHelper.formatDate( document.getStartDate() ) );
    }

    private void fillCustomerParameters(Map<String, Object> parameters, Profile client, Customer customer, boolean clientIsCustomer) throws DataNotFoundException {
        if ( clientIsCustomer ) {
            parameters.put( "customerName", getFieldText(client.getName(), "не указан") );
            parameters.put( "customerAddress", getFieldText(client.getAddress(), "не указан") );
            parameters.put( "customerPhone", getFieldText(client.getPhone(), "не указан") );
        }
        else {
            parameters.put( "customerName", getFieldText(customer.getName(), "не указан") );
            parameters.put( "customerAddress", getFieldText(customer.getAddress(), "не указан") );
            parameters.put( "customerPhone", getFieldText(customer.getPhone(), "не указан") );
        }
    }

    private void fillVehicleParameters(Map<String, Object> parameters, Vehicle vehicle, VehicleMileage vehicleMileage) throws DataNotFoundException {
        parameters.put( "vehicleName", getFieldText(vehicle.getModelName(), "не указана") );
        parameters.put( "vehicleRegNum", getFieldText(vehicle.getRegNumber(), "не указан") );
        parameters.put( "vehicleVinNum", getFieldText(vehicle.getVinNumber(), "не указан") );
        parameters.put( "vehicleYear", vehicle.getYear() );
        if ( vehicleMileage != null )
            parameters.put( "vehicleMileage", vehicleMileage.getMileage() );
    }

    private void fillRepairParameters(Map<String, Object> parameters, ServiceDocument document) throws DataNotFoundException {
        parameters.put( "repairReason", getFieldText(document.getReason(), "не указана") );
    }

    private List<Map<String, Object>> getServiceWorkData(Map<String, Object> parameters, ServiceDocument document) throws DataNotFoundException {

        Double totalSum = 0.0;
        List<Map<String, Object>> result = new ArrayList<>();
        Set<ServiceWork> serviceWorks = document.getServiceWorks();
        Set<ServiceAddon> serviceAddons = document.getServiceAddons();

        for (ServiceWork serviceWork : serviceWorks) {
            totalSum += serviceWork.calculateServiceWorkTotalCost();
        }
        for (ServiceAddon serviceAddon : serviceAddons) {
            totalSum += serviceAddon.calculateServiceAddonTotalCost();
        }

        result.addAll( document.getServiceWorks().stream().map(ServiceWork::buildReportData).collect( Collectors.toList() ) );
        result.addAll( document.getServiceAddons().stream().map(ServiceAddon::buildReportData).collect( Collectors.toList() ) );

        fwMoney totalSumString = new fwMoney( totalSum );
        parameters.put("totalSumString", totalSumString.num2str());
        parameters.put("totalSum", totalSum);
        parameters.put("totalCount", result.size());

        return result;

    }

    private String getFieldText(String field, String returnText) {
        if ( field == null || field.length() == 0 )
            return returnText;

        return field;
    }

    /* ------------------------------- STATISTICS REPORTS ------------------------------- */

    @Override
    public List<ClientResponse> getClientsResponses(Long profileId, Date startDate, Date endDate) {
        List<ClientResponse> clientResponses = new ArrayList<>();
        List<ServiceDocument> serviceDocuments;

        if ( startDate != null && endDate != null )
            serviceDocuments = serviceDocumentRepository.findByStartDateBetweenAndExecutorIdOrderByStartDate(startDate, endDate, profileId);
        else
            serviceDocuments = serviceDocumentRepository.findByExecutorIdOrderByStartDate(profileId);

        for (ServiceDocument serviceDocument : serviceDocuments) {
            Profile client = serviceDocument.getClient();

            if ( client == null ) continue;

            ClientResponse clientResponse = filterClientResponse(clientResponses, client.getId());

            if ( clientResponse == null ) {
                clientResponse = new ClientResponse(client);
                clientResponses.add( clientResponse );
            }

            clientResponse.addClientResponse( serviceDocument );
        }

        return clientResponses;
    }

    private ClientResponse filterClientResponse(List<ClientResponse> clientResponses, Long clientId) {
        if ( clientResponses.size() == 0 ) return null;

        return clientResponses
                .stream()
                .filter( clientResponse -> clientResponse.getClientId().equals( clientId ))
                .findFirst()
                .orElse(null);
    }

    @Override
    public byte[] getClientsReport(Long profileId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException {
        File template = new File( reportsCatalog + "clients.jasper" );
        InputStream templateStream = new FileInputStream(template);

        String title = String.format("Отчет о реализации за период с %s по %s", DateHelper.formatDate(startDate), DateHelper.formatDate(endDate));

        Map<String, Object> parameters = new HashMap<>();
        JRBeanCollectionDataSource reportData = new JRBeanCollectionDataSource( getClientsReportData( profileId, startDate, endDate) );
        parameters.put("reportData", reportData);
        parameters.put("reportTitle", title);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private List<Map<String, Object>> getClientsReportData(Long profileId, Date startDate, Date endDate) throws DataNotFoundException {
        List<ClientResponse> responses = getClientsResponses(profileId, startDate, endDate);
//        if ( demoDomain && responses.size() == 0 )
//            responses = getClientFakeResponses(startDate, endDate);

        if ( responses.size() == 0 ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();
        double total = 0.0;

        for ( ClientResponse response : responses ) {

            result.add( response.buildReportData() );
            total += response.getTotal();

            for ( ClientDocumentResponse clientDocumentResponse : response.getClientDocumentResponses() ) {
                result.add( clientDocumentResponse.buildReportData() );
            }

        }

        Map<String, Object> totalRow = new HashMap<>();
        totalRow.put("isBold", true);
        totalRow.put("fullName", "ИТОГ");
        totalRow.put("total", total);

        result.add( totalRow );

        return result;
    }

    @Override
    public List<ClientResponse> getVehiclesResponses(Long profileId, String vinNumber, Date startDate, Date endDate) {
        List<ClientResponse> clientResponses = new ArrayList<>();
        List<ServiceDocument> serviceDocuments;

        if ( startDate != null && endDate != null )
            serviceDocuments = serviceDocumentRepository.findByVehicleVinNumberAndStartDateBetweenAndExecutorId(startDate, endDate, profileId, "%" + vinNumber + "%");
        else
            serviceDocuments = serviceDocumentRepository.findByVehicleVinNumberAndExecutorId(profileId, "%" + vinNumber + "%");

        for (ServiceDocument serviceDocument : serviceDocuments) {
            Vehicle vehicle = serviceDocument.getVehicle();

            if ( vehicle == null ) continue;

            ClientResponse clientResponse = filterClientResponse(clientResponses, vehicle.getId());

            if ( clientResponse == null ) {
                clientResponse = new ClientResponse(vehicle);
                clientResponses.add( clientResponse );
            }

            clientResponse.addClientResponse( serviceDocument );
        }

        return clientResponses;
    }

    @Override
    public byte[] getVehiclesReport(Long profileId, String vinNumber, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException {
        File template = new File( reportsCatalog + "clients.jasper" );
        InputStream templateStream = new FileInputStream(template);

        String title;

        if ( startDate != null && endDate != null )
            title = String.format("Отчет о ремонтах автомобиля за период с %s по %s", DateHelper.formatDate(startDate), DateHelper.formatDate(endDate));
        else
            title = "Отчет о ремонтах автомобиля";

        Map<String, Object> parameters = new HashMap<>();
        JRBeanCollectionDataSource reportData = new JRBeanCollectionDataSource( getVehiclesReportData( profileId, vinNumber, startDate, endDate) );
        parameters.put("reportData", reportData);
        parameters.put("reportTitle", title);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private List<Map<String, Object>> getVehiclesReportData(Long profileId, String vinNumber, Date startDate, Date endDate) throws DataNotFoundException {
        List<ClientResponse> responses = getVehiclesResponses(profileId, vinNumber, startDate, endDate);
//        if ( demoDomain && responses.size() == 0 )
//            responses = getClientFakeResponses(startDate, endDate);

        if ( responses.size() == 0 ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();
        double total = 0.0;

        for ( ClientResponse response : responses ) {

            result.add( response.buildReportData() );
            total += response.getTotal();

            for ( ClientDocumentResponse clientDocumentResponse : response.getClientDocumentResponses() ) {
                result.add( clientDocumentResponse.buildReportData() );
            }

        }

        Map<String, Object> totalRow = new HashMap<>();
        totalRow.put("isBold", true);
        totalRow.put("fullName", "ИТОГ");
        totalRow.put("total", total);

        result.add( totalRow );

        return result;
    }

    @Override
    public List<ExecutorResponse> getRegisteredResponses(Long profileId) {
        List<ExecutorResponse> executorResponses = new ArrayList<>();
        List<Profile> clients;

        if ( profileId != null )
            clients = profileRepository.findClientsByCreatedBy(profileId);
        else
            clients = profileRepository.findClients();

        for (Profile client : clients) {
            Profile createdBy = client.getCreatedBy();

            if ( createdBy == null ) {
                ExecutorResponse executorResponse = filterExecutorResponse(executorResponses, null);

                if ( executorResponse == null ) {
                    executorResponse = new ExecutorResponse("Самостоятельная регистрация");
                    executorResponses.add( executorResponse );
                }

                executorResponse.addRegisteredResponse( client );
            }
            else {
                ExecutorResponse executorResponse = filterExecutorResponse(executorResponses, createdBy.getId());

                if ( executorResponse == null ) {
                    executorResponse = new ExecutorResponse(createdBy);
                    executorResponses.add( executorResponse );
                }

                executorResponse.addRegisteredResponse( client );
            }
        }

        return executorResponses;
    }

    private ExecutorResponse filterExecutorResponse(List<ExecutorResponse> executorResponses, Long registeredById) {
        if ( executorResponses.size() == 0 ) return null;

        return executorResponses
                .stream()
                .filter( clientResponse -> clientResponse.getRegisteredById() == null && registeredById == null ||
                        ( clientResponse.getRegisteredById() != null && clientResponse.getRegisteredById().equals( registeredById ) ) )
                .findFirst()
                .orElse(null);
    }

    @Override
    public byte[] getRegisteredReport(Long profileId) throws IOException, JRException, DataNotFoundException {
        File template = new File( reportsCatalog + "registered.jasper" );
        InputStream templateStream = new FileInputStream(template);

        String title = "Отчет о зарегистрированных клиентах";

        Map<String, Object> parameters = new HashMap<>();
        JRBeanCollectionDataSource reportData = new JRBeanCollectionDataSource( getRegisteredReportData( profileId ) );
        parameters.put("reportData", reportData);
        parameters.put("reportTitle", title);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private List<Map<String, Object>> getRegisteredReportData(Long profileId) throws DataNotFoundException {
        List<ExecutorResponse> responses = getRegisteredResponses(profileId);
//        if ( demoDomain && responses.size() == 0 )
//            responses = getClientFakeResponses(startDate, endDate);

        if ( responses.size() == 0 ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();

        for ( ExecutorResponse response : responses ) {

            result.add( response.buildReportData() );

            for ( ClientRegisteredResponse clientDocumentResponse : response.getClientRegisteredResponses() ) {
                result.add( clientDocumentResponse.buildReportData() );
            }

        }

        return result;
    }
}
