package io.swagger.service.impl;

import io.swagger.helper.DateHelper;
import io.swagger.helper.fwMoney;
import io.swagger.postgres.model.*;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.repository.ServiceDocumentRepository;
import io.swagger.response.exception.DataNotFoundException;
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

    @Value("${reports.catalog}")
    private String reportsCatalog;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @Override
    public byte[] getOrderReport(Long documentId) throws IOException, JRException, DataNotFoundException {

        ServiceDocument document = serviceDocumentRepository.findById( documentId ).orElse(null);
        if ( document == null ) throw new DataNotFoundException();

        String orderName = "orderSimple.jasper";

        File template = new File( reportsCatalog + orderName );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderReportParameters( parameters, document );
        JRBeanCollectionDataSource serviceData = new JRBeanCollectionDataSource( getServiceWorkData( parameters, document ) );
        parameters.put("serviceData", serviceData);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderReportParameters(Map<String, Object> parameters, ServiceDocument document) throws DataNotFoundException {

        Vehicle vehicle = document.getVehicle();
        if ( vehicle == null ) throw new DataNotFoundException();

        VehicleMileage vehicleMileage = document.getVehicleMileage();
        if ( vehicleMileage == null ) throw new DataNotFoundException();

        Profile executioner = document.getExecutor();
        if ( executioner == null ) throw new DataNotFoundException();

        Profile client = document.getClient();
        if ( client == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, executioner);
        fillOrderParameters(parameters, document);
        fillCustomerParameters(parameters, client);
        fillVehicleParameters(parameters, vehicle, vehicleMileage);
        fillRepairParameters(parameters, document);

    }

    private void fillOrganizationParameters(Map<String, Object> parameters, Profile executioner) throws DataNotFoundException {
        parameters.put( "organizationName", getFieldText(executioner.getName(), "не указан") );
        parameters.put( "organizationInn", getFieldText(executioner.getInn(), "не указан") );
        parameters.put( "organizationAddress", getFieldText(executioner.getAddress(), "не указан") );
        parameters.put( "organizationPhone", getFieldText(executioner.getPhone(), "не указан") );
        parameters.put( "organizationEmail", getFieldText(executioner.getEmail(), "не указан") );
        if ( executioner.getUser() != null )
            parameters.put( "executionerFio", executioner.getUser().getFio() );
    }

    private void fillOrderParameters(Map<String, Object> parameters, ServiceDocument document) {
        parameters.put( "orderNum", document.getNumber() );
        parameters.put( "orderStartDate", DateHelper.formatDateTime( document.getStartDate() ) );
    }

    private void fillCustomerParameters(Map<String, Object> parameters, Profile client) throws DataNotFoundException {
        parameters.put( "customerName", getFieldText(client.getName(), "не указан") );
        parameters.put( "customerAddress", getFieldText(client.getAddress(), "не указан") );
        parameters.put( "customerPhone", getFieldText(client.getPhone(), "не указан") );
    }

    private void fillVehicleParameters(Map<String, Object> parameters, Vehicle vehicle, VehicleMileage vehicleMileage) throws DataNotFoundException {
        parameters.put( "vehicleName", getFieldText(vehicle.getModelName(), "не указана") );
        parameters.put( "vehicleRegNum", getFieldText(vehicle.getRegNumber(), "не указан") );
        parameters.put( "vehicleVinNum", getFieldText(vehicle.getVinNumber(), "не указан") );
        parameters.put( "vehicleYear", vehicle.getYear() );
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

        return result;

    }

    private String getFieldText(String field, String returnText) {
        if ( field == null || field.length() == 0 )
            return returnText;

        return field;
    }
}
