package io.swagger.service.impl;

import io.swagger.firebird.model.*;
import io.swagger.firebird.repository.ActDefectionCheckRepository;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.helper.DateHelper;
import io.swagger.helper.ReportHelper;
import io.swagger.helper.fwMoney;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.*;
import io.swagger.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final static Logger logger = LoggerFactory.getLogger( ReportService.class );

    private final String[] EXECUTORS = new String[] {"Иванов И.И", "Петров П.П", "Сидоров С.С"};
    private final String[] CLIENTS = new String[] {"ООО \"Вектор\"", "Савельев С.П", "Богатов П.В"};

    @Autowired
    private DocumentServiceDetailRepository documentServiceDetailRepository;
    @Autowired
    private ActDefectionCheckRepository actDefectionCheckRepository;

    @Value("${reports.catalog}")
    private String reportsCatalog;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @Override
    public byte[] getOrderReport(Integer documentId, Boolean printStamp) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "order.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderReportParameters( parameters, document, printStamp );
        JRBeanCollectionDataSource serviceWorkData = new JRBeanCollectionDataSource( getServiceWorkData( parameters, document ) );
        JRBeanCollectionDataSource serviceGoodsData = new JRBeanCollectionDataSource( getServiceGoodsData( parameters, document ) );
        JRBeanCollectionDataSource clientGoodsData = new JRBeanCollectionDataSource( getClientGoodsData( document ) );
        parameters.put("serviceWorkData", serviceWorkData);
        parameters.put("serviceGoodsData", serviceGoodsData);
        parameters.put("clientGoodsData", clientGoodsData);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderReportParameters(Map<String, Object> parameters, DocumentServiceDetail document, Boolean printStamp) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillBarCode(parameters, documentOutHeader);
        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, true, true);
        fillCustomerParameters(parameters, documentOut, true, true);
        fillVehicleParameters(parameters, document, false, true, false);
        fillRepairParameters(parameters, document, true, true, true);

        parameters.put( "employeeFio", employee.getShortName() );

        if ( printStamp ) {
            try {

                Organization organization = documentOut.getOrganization();
                if ( organization == null ) throw new DataNotFoundException();

                ByteArrayInputStream bais = new ByteArrayInputStream( organization.getStampSource() );
                BufferedImage bufferedImage = ImageIO.read( bais );

                parameters.put("stamp", bufferedImage);

            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public byte[] getOrderActReport(Integer documentId, Boolean printStamp) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderAct.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderActReportParameters( parameters, document, printStamp );
        JRBeanCollectionDataSource serviceWorkData = new JRBeanCollectionDataSource( getServiceWorkData( parameters, document ) );
        JRBeanCollectionDataSource serviceGoodsData = new JRBeanCollectionDataSource( getServiceGoodsData( parameters, document ) );
        parameters.put("serviceWorkData", serviceWorkData);
        parameters.put("serviceGoodsData", serviceGoodsData);

        Double workSum = (Double) parameters.get("workSum");
        Double goodsSum = (Double) parameters.get("goodsSum");

        parameters.put("totalSumString", new fwMoney( workSum + goodsSum ).num2str(false));

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderActReportParameters(Map<String, Object> parameters, DocumentServiceDetail document, Boolean printStamp) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillBarCode(parameters, documentOutHeader);
        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, true, false);
        fillCustomerParameters(parameters, documentOut, true, false);
        fillVehicleParameters(parameters, document, false, true, false);
        fillRepairParameters(parameters, document, false, false, true);

        if ( printStamp ) {
            try {

                Organization organization = documentOut.getOrganization();
                if ( organization == null ) throw new DataNotFoundException();

                ByteArrayInputStream bais = new ByteArrayInputStream( organization.getStampSource() );
                BufferedImage bufferedImage = ImageIO.read( bais );

                parameters.put("stamp", bufferedImage);

            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public byte[] getOrderRequestReport(Integer documentId) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderRequest.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderRequestReportParameters( parameters, document );

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderRequestReportParameters(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, true, false);
        fillCustomerParameters(parameters, documentOut, true, true);
        fillVehicleParameters(parameters, document, false, false, false);
        fillRepairParameters(parameters, document, false, true, false);

    }

    @Override
    public byte[] getOrderTaskReport(Integer documentId) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderTask.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderTaskReportParameters( parameters, document );
        JRBeanCollectionDataSource serviceWorkData = new JRBeanCollectionDataSource( getServiceWorkShortData( document ) );
        JRBeanCollectionDataSource serviceGoodsData = new JRBeanCollectionDataSource( getServiceGoodsShortData( document ) );
        JRBeanCollectionDataSource clientGoodsData = new JRBeanCollectionDataSource( getClientGoodsShortData( document ) );
        parameters.put("serviceWorkData", serviceWorkData);
        parameters.put("serviceGoodsData", serviceGoodsData);
        parameters.put("clientGoodsData", clientGoodsData);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderTaskReportParameters(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, false, false);
        fillCustomerParameters(parameters, documentOut, true, true);
        fillVehicleParameters(parameters, document, false, false, false);
        fillRepairParameters(parameters, document, true, true, true);

    }

    @Override
    public byte[] getOrderInspectionReport(Integer documentId) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderInspection.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        JRBeanCollectionDataSource clientGoodsData = new JRBeanCollectionDataSource( getClientGoodsData( document ) );
        parameters.put("clientGoodsData", clientGoodsData);
        fillOrderInspectionReportParameters( parameters, document );

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderInspectionReportParameters(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, true, false);
        fillCustomerParameters(parameters, documentOut, true, true);
        fillVehicleParameters(parameters, document, false, false, false);
        fillRepairParameters(parameters, document, false, true, false);

        parameters.put( "vehicleFuelLevel", document.getIndexFuel() != null ? document.getIndexFuel() : 0 );
        parameters.put( "fuelLevelImage", ReportHelper.drawFuelLevel( document.getIndexFuel() ) );
        parameters.put( "structure", document.getStructureCar() );

        if ( document.getLkp() != null ) {

            switch ( document.getLkp() ) {
                case 0: parameters.put("lkpStatus", "Дефекты ЛКП указаны."); break;
                case 1: parameters.put("lkpStatus", "Дефекты ЛКП не указаны."); break;
                case 2: parameters.put("lkpStatus", "А/м загрязнен, созможны скрытые дефекты ЛКП."); break;
                case 3: parameters.put("lkpStatus", "А/м оставил без осмотра, претензии к повреждениям ЛКП не имею."); break;
            }

        }

        try {

            InspectionCarImage carImage = document.getInspectionCarImage();

            ByteArrayInputStream bais = new ByteArrayInputStream( carImage.getPictureSource() );
            BufferedImage bufferedImage = ImageIO.read( bais );

            List<ExternalTestDetail> externalTestDetails = ReportHelper.buildExternalTestDetails( document.getExternalTest() );

            if ( externalTestDetails.size() > 0 ) {
                bufferedImage = ReportHelper.processInspectionImage( bufferedImage, externalTestDetails );
            }

            parameters.put("inspectionImage", bufferedImage);

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] getOrderRequirementReport(Integer documentId) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderRequirement.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderRequirementReportParameters( parameters, document );

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderRequirementReportParameters(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, true, false);
        fillCustomerParameters(parameters, documentOut, false, false);
        fillVehicleParameters(parameters, document, true, false, false);

    }

    @Override
    public byte[] getOrderReceiptReport(Integer documentId) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderReceipt.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderReceiptReportParameters( parameters, document );

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderReceiptReportParameters(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, false, false);
        fillCustomerParameters(parameters, documentOut, false, false);
        fillVehicleParameters(parameters, document, false, false, true);

        parameters.put("receiptDate", DateHelper.formatDate( new Date() ));

    }

    @Override
    public byte[] getOrderDefectionReport(Integer documentId) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderDefection.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderDefectionReportParameters( parameters, document );
        JRBeanCollectionDataSource defectionData = new JRBeanCollectionDataSource( getDefectionData( parameters, document ) );
        parameters.put("defectionData", defectionData);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderDefectionReportParameters(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        fillBarCode(parameters, documentOutHeader);
        fillOrganizationParameters(parameters, documentOut);
        fillOrderParameters(parameters, document, documentOutHeader, true, true);
        fillCustomerParameters(parameters, documentOut, true, true);
        fillVehicleParameters(parameters, document, false, true, false);

    }

    @Override
    public byte[] getOrderTransferReport(Integer documentId, Boolean printStamp) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "orderTransfer.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillOrderTransferReportParameters( parameters, document, printStamp );
        JRBeanCollectionDataSource transferData = new JRBeanCollectionDataSource( getTransferData( document ) );
        parameters.put("workData", transferData);
        parameters.put("subReportFile", reportsCatalog + "orderTransferSub.jasper");

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillOrderTransferReportParameters(Map<String, Object> parameters, DocumentServiceDetail document, Boolean printStamp) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        Organization organization = documentOut.getOrganization();
        if ( organization == null ) throw new DataNotFoundException();

        fillBarCode(parameters, documentOutHeader);
        Contact organizationContact = documentOut.getOrganizationContact();

        parameters.put( "currency", "Российский рубль, 643" );

        parameters.put( "organizationName", organization.getFullName() );
        if ( organizationContact != null ) {
            parameters.put( "organizationAddress", organizationContact.getContactFull() );
        }

        parameters.put( "orderNum", documentOutHeader.getNumber() != null ? String.format( "СФ-%s", documentOutHeader.getNumber() ) : "не указан" );
        parameters.put( "orderDate", DateHelper.formatDateTime( document.getDateStart() ) );

        Client client = documentOut.getClient();
        if ( client == null ) throw new DataNotFoundException();

        Contact clientContact = documentOut.getClientContact();

        parameters.put( "clientName", client.getFullName() );
        if ( clientContact != null ) {
            parameters.put( "clientAddress", clientContact.getContactFull() );
        }

        if ( printStamp ) {
            try {

                ByteArrayInputStream bais = new ByteArrayInputStream( organization.getStampSource() );
                BufferedImage bufferedImage = ImageIO.read( bais );

                parameters.put("stamp", bufferedImage);

            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }

    }

    private List<Map<String, Object>> getTransferData(DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();
        int number = 0;

        for ( ServiceGoodsAddon serviceGoodsAddon : documentOut.getServiceGoodsAddons() ) {
            result.add( serviceGoodsAddon.buildTransferReportData(++number) );
        }
        for ( ServiceWork serviceWork : documentOut.getServiceWorks() ) {
            result.add( serviceWork.buildTransferReportData(++number) );
        }
        //TODO: добавить GoodsOut

        return result;

    }

    private void fillBarCode(Map<String, Object> parameters, DocumentOutHeader documentOutHeader) throws DataNotFoundException {
        parameters.put("barCode", documentOutHeader.getBarcode());
    }

    private void fillOrganizationParameters(Map<String, Object> parameters, DocumentOut documentOut) throws DataNotFoundException {

        Organization organization = documentOut.getOrganization();
        if ( organization == null ) throw new DataNotFoundException();

        Contact organizationContact = documentOut.getOrganizationContact();

        parameters.put( "organizationName", organization.getFullName() );
        if ( organizationContact != null )
            parameters.put( "organizationAddress", organizationContact.getContactFull() );

    }

    private void fillOrderParameters(Map<String, Object> parameters, DocumentServiceDetail document,
                                     DocumentOutHeader documentOutHeader, boolean fillStartDate, boolean fillEndDate) {

        parameters.put( "orderNum", documentOutHeader.getFullNumber() != null ? documentOutHeader.getFullNumber() : "не указан" );
        if ( fillStartDate )
            parameters.put( "orderStartDate", DateHelper.formatDateTime( document.getDateStart() ) );
        if ( fillEndDate )
            parameters.put( "orderEndDate", DateHelper.formatDateTime( documentOutHeader.getDateCreate() ) );

    }

    private void fillCustomerParameters(Map<String, Object> parameters, DocumentOut documentOut,
                                        boolean fillAddress, boolean fillPhone) throws DataNotFoundException {

        Client client = documentOut.getClient();
        if ( client == null ) throw new DataNotFoundException();

        Contact clientContact = documentOut.getClientContact();

        parameters.put( "customerName", client.getFullName() );
        if ( fillAddress && clientContact != null )
            parameters.put( "customerAddress", clientContact.getContactFull() );
        if ( fillPhone && clientContact != null )
            parameters.put( "customerPhone", clientContact.getMobile() );

    }

    private void fillVehicleParameters(Map<String, Object> parameters, DocumentServiceDetail document,
                                       boolean fillShortData, boolean fillAmts, boolean fillSTS) throws DataNotFoundException {

        ModelLink modelLink = document.getModelLink();
        if ( modelLink == null ) throw new DataNotFoundException();

        ModelDetail modelDetail = modelLink.getModelDetail();
        if ( modelDetail == null ) throw new DataNotFoundException();

        Model model = modelDetail.getModel();
        if ( model == null ) throw new DataNotFoundException();

        parameters.put( "vehicleName", model.getFullName() );
        parameters.put( "vehicleRegNum", modelDetail.getNormalizedRegNumber() );
        parameters.put( "vehicleVinNum", modelDetail.getVinNumber() );

        if ( !fillShortData ) {
            parameters.put( "vehicleYear", DateHelper.formatYear( modelDetail.getYearOfProduction() ) );
            parameters.put( "vehicleEngNum", modelDetail.getEngineNumber() );
            parameters.put( "vehicleChassisNum", modelDetail.getChassisNumber() );
            parameters.put( "vehicleColor", modelDetail.getColorName() );
            parameters.put( "vehicleType", modelDetail.getCarEngineTypeName() );
            parameters.put( "vehicleMileage", document.getRunBefore() );
            if ( fillAmts )
                parameters.put( "vehicleAmts", document.getCarCost() );
        }

        if ( fillSTS ) {
            // TODO: найти и добавить заполнение информации о СТС
        }

    }

    private void fillRepairParameters(Map<String, Object> parameters, DocumentServiceDetail document,
                                      boolean fillType, boolean fillReason, boolean fillSpecial) throws DataNotFoundException {

        if ( fillType )
            parameters.put( "repairType", document.getRepairTypeName() );
        if ( fillReason )
            parameters.put( "repairReason", document.getReasonsAppeal() );
        if ( fillSpecial )
            parameters.put( "repairSpecial", document.getSpecialNotes() );

    }

    private List<Map<String, Object>> getServiceWorkData(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        Double workSum = 0.0;
        for (ServiceWork serviceWork : documentOut.getServiceWorks()) {
            workSum += serviceWork.getServiceWorkTotalCost(true);
        }
        parameters.put("workSum", workSum);

        return documentOut.getServiceWorks().stream().map(ServiceWork::buildReportData).collect( Collectors.toList() );

    }

    private List<Map<String, Object>> getServiceGoodsData(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        Double goodsSum = 0.0;
        for (ServiceGoodsAddon serviceGoodsAddon : documentOut.getServiceGoodsAddons()) {
            goodsSum += serviceGoodsAddon.getServiceGoodsCost(true);
        }
        parameters.put("goodsSum", goodsSum);

        return documentOut.getServiceGoodsAddons().stream().map(ServiceGoodsAddon::buildReportData).collect( Collectors.toList() );

    }

    private List<Map<String, Object>> getClientGoodsData(DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        return documentOut.getGoodsOutClients().stream().map(GoodsOutClient::buildReportData).collect( Collectors.toList() );

    }

    private List<Map<String, Object>> getDefectionData(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        List<ActDefectionCheck> actDefectionChecks = actDefectionCheckRepository.findParents();
        List<Map<String, Object>> result = new ArrayList<>();
        boolean isFirst = true;

        for (ActDefectionCheck actDefectionCheck : actDefectionChecks) {

            result.add( actDefectionCheck.buildReportData(isFirst) );
            if ( isFirst ) isFirst = false;

            List<ActDefectionCheck> actDefectionChecksChildren = actDefectionCheckRepository.findChildren( actDefectionCheck.getId() );

            for (ActDefectionCheck actDefectionChecksChild : actDefectionChecksChildren) {
                result.add( actDefectionChecksChild.buildReportData(isFirst) );
            }

        }

        return result;

    }

    private List<Map<String, Object>> getServiceWorkShortData(DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();

        for (ServiceWork serviceWork : documentOut.getServiceWorks()) {
            result.add( serviceWork.buildShortReportData() );
        }

        for ( int count = 0; count < 5; count++ ) {
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("workCode", null);
            emptyData.put("workName", null);
            emptyData.put("quantity", null);
            emptyData.put("timeValue", null);
            emptyData.put("executor", null);
            result.add( emptyData );
        }

        return result;

    }

    private List<Map<String, Object>> getServiceGoodsShortData(DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();

        for (ServiceGoodsAddon serviceGoodsAddon : documentOut.getServiceGoodsAddons()) {
            result.add( serviceGoodsAddon.buildShortReportData() );
        }

        for ( int count = 0; count < 5; count++ ) {
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("number", null);
            emptyData.put("name", null);
            emptyData.put("unit", null);
            emptyData.put("count", null);
            result.add( emptyData );
        }

        return result;

    }

    private List<Map<String, Object>> getClientGoodsShortData(DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();

        for (GoodsOutClient goodsOutClient : documentOut.getGoodsOutClients()) {
            result.add( goodsOutClient.buildReportData() );
        }

        for ( int count = 0; count < 5; count++ ) {
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("number", null);
            emptyData.put("name", null);
            emptyData.put("count", null);
            result.add( emptyData );
        }

        return result;

    }

    @Override
    public byte[] getExecutorsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException {

        File template = new File( reportsCatalog + "executors.jasper" );
        InputStream templateStream = new FileInputStream(template);

        String title = String.format("Выручка по слесарям за период с %s по %s", DateHelper.formatDate(startDate), DateHelper.formatDate(endDate));

        Map<String, Object> parameters = new HashMap<>();
        JRBeanCollectionDataSource reportData = new JRBeanCollectionDataSource( getExecutorsReportData( organizationId, startDate, endDate) );
        parameters.put("reportData", reportData);
        parameters.put("reportTitle", title);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private List<Map<String, Object>> getExecutorsReportData(Integer organizationId, Date startDate, Date endDate) throws DataNotFoundException {
        List<ExecutorResponse> responses = getExecutorResponses(organizationId, startDate, endDate);
        if ( demoDomain && responses.size() == 0 )
            responses = getExecutorFakeResponses(startDate, endDate);

        if ( responses.size() == 0 ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();
        double totalByNorm = 0.0;
        double totalByPrice = 0.0;
        double totalSum = 0.0;
        double totalSalary = 0.0;

        for ( ExecutorResponse response : responses ) {

            result.add( response.buildReportData() );
            totalByNorm += response.getTotalByNorm();
            totalByPrice += response.getTotalByPrice();
            totalSum += response.getTotalSum();
            totalSalary += response.getSalary();

            int index = 0;
            for ( ExecutorDocumentResponse executorDocumentResponse : response.getExecutorDocumentResponses() ) {
                result.add( executorDocumentResponse.buildReportData( ++index ) );
            }

        }

        Map<String, Object> totalRow = new HashMap<>();
        totalRow.put("isBold", true);
        totalRow.put("fullName", "ИТОГ");
        totalRow.put("totalByNorm", totalByNorm);
        totalRow.put("totalByPrice", totalByPrice);
        totalRow.put("totalSum", totalSum);
        totalRow.put("salary", totalSalary);

        result.add( totalRow );

        return result;
    }

    @Override
    public byte[] getClientsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException {

        File template = new File( reportsCatalog + "clients.jasper" );
        InputStream templateStream = new FileInputStream(template);

        String title = String.format("Отчет о реализации за период с %s по %s", DateHelper.formatDate(startDate), DateHelper.formatDate(endDate));

        Map<String, Object> parameters = new HashMap<>();
        JRBeanCollectionDataSource reportData = new JRBeanCollectionDataSource( getClientsReportData( organizationId, startDate, endDate) );
        parameters.put("reportData", reportData);
        parameters.put("reportTitle", title);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private List<Map<String, Object>> getClientsReportData(Integer organizationId, Date startDate, Date endDate) throws DataNotFoundException {
        List<ClientResponse> responses = getClientsResponses(organizationId, startDate, endDate);
        if ( demoDomain && responses.size() == 0 )
            responses = getClientFakeResponses(startDate, endDate);

        if ( responses.size() == 0 ) throw new DataNotFoundException();

        List<Map<String, Object>> result = new ArrayList<>();
        double total = 0.0;

        for ( ClientResponse response : responses ) {

            result.add( response.buildReportData() );
            total += response.getTotal();

            int index = 0;
            for ( ClientDocumentResponse clientDocumentResponse : response.getClientDocumentResponses() ) {
                result.add( clientDocumentResponse.buildReportData( ++index ) );
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
    public List<ExecutorResponse> getExecutorResponses(Integer organizationId, Date startDate, Date endDate) {

        List<ExecutorsNativeResponse> responses = documentServiceDetailRepository.findExecutors(organizationId, startDate, endDate);
        List<ExecutorResponse> executorResponses = new ArrayList<>();

        for (ExecutorsNativeResponse response : responses) {

            ExecutorResponse executorResponse = filterExecutorResponses( executorResponses, response );

            if ( executorResponse == null ) {
                executorResponse = new ExecutorResponse( response );
                executorResponses.add( executorResponse );
            }
            else {
                executorResponse.increaseTotalByNorm( response.getTotalByNorm() );
                executorResponse.increaseTotalByPrice( response.getTotalByPrice() );
            }

            executorResponse.addDocumentResponse( response );

        }

        return executorResponses;
    }

    @Override
    public List<ExecutorResponse> getExecutorFakeResponses(Date startDate, Date endDate) {

        List<ExecutorResponse> executorResponses = new ArrayList<>();

        for ( String executor : EXECUTORS ) {

            double byNorm = generateRandomDouble( 0.0, 10000.0 );
            double byPrice = generateRandomDouble( 0.0, 10000.0 );
            double percent = generateRandomDouble( 45.0, 100.0 );

            ExecutorResponse fakeResponse = new ExecutorResponse();
            fakeResponse.setFullName( executor );
            fakeResponse.setTotalByNorm( byNorm );
            fakeResponse.setTotalByPrice( byPrice );
            fakeResponse.setPercent( percent );

            int totalDocuments = generateRandomInteger( 1, 10 );
            for ( int i = 1; i <= totalDocuments; i++ ) {

                ExecutorDocumentResponse fakeDocumentResponse = new ExecutorDocumentResponse();
                fakeDocumentResponse.setDocumentDate( generateRandomDate( startDate, endDate ) );
                fakeDocumentResponse.setPercent( percent );
                fakeDocumentResponse.setTotalByNorm( byNorm / totalDocuments );
                fakeDocumentResponse.setTotalByPrice( byPrice / totalDocuments );

                fakeResponse.addDocumentResponse( fakeDocumentResponse );

            }

            fakeResponse.getExecutorDocumentResponses().sort( (o1, o2) -> o1.getDocumentDate().before( o2.getDocumentDate() ) ? 1 : 0 );
            executorResponses.add( fakeResponse );

        }

        return executorResponses;
    }

    @Override
    public List<ClientResponse> getClientFakeResponses(Date startDate, Date endDate) {

        List<ClientResponse> clientResponses = new ArrayList<>();

        for ( String client : CLIENTS ) {

            double total = generateRandomDouble( 0.0, 10000.0 );

            ClientResponse fakeResponse = new ClientResponse();
            fakeResponse.setFullName( client );
            fakeResponse.setTotal( total );

            int totalDocuments = generateRandomInteger( 1, 10 );
            for ( int i = 1; i <= totalDocuments; i++ ) {

                ClientDocumentResponse fakeDocumentResponse = new ClientDocumentResponse();
                fakeDocumentResponse.setDocumentDate( generateRandomDate( startDate, endDate ) );
                fakeDocumentResponse.setTotal( total / totalDocuments );

                fakeResponse.addClientResponse( fakeDocumentResponse );

            }

            fakeResponse.getClientDocumentResponses().sort( (o1, o2) -> o1.getDocumentDate().before( o2.getDocumentDate() ) ? 1 : 0 );
            clientResponses.add( fakeResponse );

        }

        return clientResponses;
    }

    private Double generateRandomDouble(Double min, Double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    private Integer generateRandomInteger(Integer min, Integer max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private Date generateRandomDate(Date min, Date max) {
        return new Date(ThreadLocalRandom.current()
                .nextLong(min.getTime(), max.getTime()));
    }

    private ExecutorResponse filterExecutorResponses(List<ExecutorResponse> executorResponses, ExecutorsNativeResponse response) {

        if ( executorResponses.size() == 0 ) return null;

        return executorResponses.stream()
                .filter( executorResponse -> executorResponse.getFullName().equals( response.getFullName() ) )
                .findAny().orElse( null );

    }

    @Override
    public List<ClientResponse> getClientsResponses(Integer organizationId, Date startDate, Date endDate) {

        List<ClientsNativeResponse> responses = documentServiceDetailRepository.findClients(organizationId, startDate, endDate);
        List<ClientResponse> clientResponses = new ArrayList<>();

        for (ClientsNativeResponse response : responses) {

            ClientResponse clientResponse = filterClientResponses( clientResponses, response );

            if ( clientResponse == null ) {
                clientResponse = new ClientResponse( response );
                clientResponses.add( clientResponse );
            }
            else
                clientResponse.increaseTotal( response.getTotal() );

            clientResponse.addClientResponse( response );

        }

        return clientResponses;
    }

    private ClientResponse filterClientResponses(List<ClientResponse> clientResponses, ClientsNativeResponse response) {

        if ( clientResponses.size() == 0 ) return null;

        return clientResponses.stream()
                .filter( clientResponse -> clientResponse.getClientId().equals( response.getClientId() ) )
                .findAny().orElse( null );

    }

}
