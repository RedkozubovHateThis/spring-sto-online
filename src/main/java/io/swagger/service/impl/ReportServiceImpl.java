package io.swagger.service.impl;

import io.swagger.firebird.model.*;
import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.helper.DateHelper;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.*;
import io.swagger.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final static Logger logger = LoggerFactory.getLogger( ReportService.class );

    private final int EX_TOTAL_ROW = 4;
    private final int EX_BODY_ROW = 3;
    private final int EX_DATE_NAME_COLUMN = 1;
    private final int EX_TOTAL_NORM_COLUMN = 2;
    private final int EX_TOTAL_PRICE_COLUMN = 3;
    private final int EX_TOTAL_COLUMN = 4;
    private final int EX_PERCENT_COLUMN = 5;
    private final int EX_SALARY_COLUMN = 6;
    private final int EX_TITLE_ROW = 1;
    private final int EX_TITLE_COLUMN = 1;

    private final int CL_TOTAL_ROW = 4;
    private final int CL_BODY_ROW = 3;
    private final int CL_DATE_NAME_COLUMN = 1;
    private final int CL_TOTAL_COLUMN = 2;
    private final int CL_TITLE_ROW = 1;
    private final int CL_TITLE_COLUMN = 1;

    private final String[] EXECUTORS = new String[] {"Иванов И.И", "Петров П.П", "Сидоров С.С"};
    private final String[] CLIENTS = new String[] {"ООО \"Вектор\"", "Савельев С.П", "Богатов П.В"};

    @Autowired
    private DocumentServiceDetailRepository documentServiceDetailRepository;

    @Value("${reports.catalog}")
    private String reportsCatalog;

    @Override
    public byte[] getOrderReport(Integer documentId) throws IOException, JRException, DataNotFoundException {

        DocumentServiceDetail document = documentServiceDetailRepository.findOne( documentId );
        if ( document == null ) throw new DataNotFoundException();

        File template = new File( reportsCatalog + "order.jasper" );
        InputStream templateStream = new FileInputStream(template);

        Map<String, Object> parameters = new HashMap<>();
        fillReportParameters( parameters, document );
        JRBeanCollectionDataSource serviceWorkData = new JRBeanCollectionDataSource( getServiceWorkData( parameters, document ) );
        JRBeanCollectionDataSource serviceGoodsData = new JRBeanCollectionDataSource( getServiceGoodsData( parameters, document ) );
        JRBeanCollectionDataSource clientGoodsData = new JRBeanCollectionDataSource( getClientGoodsData( document ) );
        parameters.put("serviceWorkData", serviceWorkData);
        parameters.put("serviceGoodsData", serviceGoodsData);
        parameters.put("clientGoodsData", clientGoodsData);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateStream, parameters, new JREmptyDataSource());

        return JasperExportManager.exportReportToPdf( jasperPrint );
    }

    private void fillReportParameters(Map<String, Object> parameters, DocumentServiceDetail document) throws DataNotFoundException {

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) throw new DataNotFoundException();

        User user = documentOutHeader.getUser();
        if ( user == null ) throw new DataNotFoundException();

        Employee employee = user.getEmployee();
        if ( employee == null ) throw new DataNotFoundException();

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) throw new DataNotFoundException();

        Organization organization = documentOut.getOrganization();
        Contact organizationContact = documentOut.getOrganizationContact();
        if ( organization == null || organizationContact == null ) throw new DataNotFoundException();

        Client client = documentOut.getClient();
        Contact clientContact = documentOut.getClientContact();
        if ( client == null || clientContact == null ) throw new DataNotFoundException();

        ModelLink modelLink = document.getModelLink();
        if ( modelLink == null ) throw new DataNotFoundException();

        ModelDetail modelDetail = modelLink.getModelDetail();
        if ( modelDetail == null ) throw new DataNotFoundException();

        Model model = modelDetail.getModel();
        if ( model == null ) throw new DataNotFoundException();

        parameters.put( "organizationName", organization.getFullName() );
        parameters.put( "organizationAddress", organizationContact.getContactFull() );

        parameters.put( "orderNum", documentOutHeader.getFullNumber() != null ? documentOutHeader.getFullNumber() : "не указан" );
        parameters.put( "orderStartDate", DateHelper.formatDateTime( document.getDateStart() ) );
        parameters.put( "orderEndDate", DateHelper.formatDateTime( documentOutHeader.getDateCreate() ) );

        parameters.put( "customerName", client.getFullName() );
        parameters.put( "customerAddress", clientContact.getContactFull() );
        parameters.put( "customerPhone", clientContact.getMobile() );

        parameters.put( "vehicleName", model.getFullName() );
        parameters.put( "vehicleYear", DateHelper.formatYear( modelDetail.getYearOfProduction() ) );
        parameters.put( "vehicleAmts", null ); // TODO: разобраться, что сюда сувать
        parameters.put( "vehicleRegNum", modelDetail.getNormalizedRegNumber() );
        parameters.put( "vehicleVinNum", modelDetail.getVinNumber() );
        parameters.put( "vehicleEngNum", modelDetail.getEngineNumber() );
        parameters.put( "vehicleChassisNum", modelDetail.getChassisNumber() );
        parameters.put( "vehicleColor", modelDetail.getColorName() );
        parameters.put( "vehicleType", modelDetail.getCarBodyTypeName() );

        parameters.put( "repairType", document.getRepairTypeName() );
        parameters.put( "repairReason", document.getReasonsAppeal() );
        parameters.put( "repairSpecial", document.getSpecialNotes() );

        parameters.put( "employeeFio", employee.getShortName() );

        try {

            ByteArrayInputStream bais = new ByteArrayInputStream( organization.getStampSource() );
            BufferedImage bufferedImage = ImageIO.read( bais );

            parameters.put("stamp", bufferedImage);

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

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

    @Override
    public byte[] getExecutorsReportPDF(Integer organizationId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException {

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
//        List<ExecutorResponse> responses = getExecutorFakeResponses(startDate, endDate);
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
    public byte[] getClientsReportPDF(Integer organizationId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException {

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
//        List<ClientResponse> responses = getClientFakeResponses(startDate, endDate);
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
    public byte[] getExecutorsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, DataNotFoundException {

        File template = new File(reportsCatalog + "executors.xlsx");
        InputStream templateStream = new FileInputStream(template);

        XSSFWorkbook workBook = new XSSFWorkbook( templateStream );
        XSSFSheet dataSheet = workBook.getSheetAt(0);

        List<ExecutorResponse> executorResponses = getExecutorResponses(organizationId, startDate, endDate);
        if ( executorResponses.size() == 0 ) throw new DataNotFoundException();

        int bodySize = executorResponses.stream()
                .mapToInt( value -> value.getExecutorDocumentResponses().size() + 1).sum();

        prepareTitle( dataSheet, startDate, endDate, "Выручка по слесарям", EX_TITLE_ROW, EX_TITLE_COLUMN );
        prepareExecutorsBody( dataSheet, bodySize );
        fillExecutorsDocuments( dataSheet, executorResponses, bodySize );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workBook.write(bos);
        return bos.toByteArray();
    }

    private void prepareExecutorsBody(XSSFSheet dataSheet, Integer bodySize) {
        if ( bodySize < 2 ) {
            logger.info("No need to prepare body, skipping...");
            return;
        }

        dataSheet.shiftRows(EX_TOTAL_ROW, EX_TOTAL_ROW, bodySize - 1);
        CellCopyPolicy policy = new CellCopyPolicy();

        for (int i = 1; i <= bodySize - 1; i++) {
            dataSheet.copyRows(EX_BODY_ROW, EX_BODY_ROW, EX_BODY_ROW + i, policy);
        }
    }

    private void fillExecutorsDocuments(XSSFSheet dataSheet, List<ExecutorResponse> executorResponses, Integer bodySize) {

        int counter = 0;

        double totalByNorm = 0.0;
        double totalByPrice = 0.0;
        double totalSum = 0.0;
        double totalSalary = 0.0;

        for ( ExecutorResponse executorResponse : executorResponses ) {

            XSSFRow executorRow = dataSheet.getRow( EX_BODY_ROW + counter++ );

            XSSFCell nameCell = executorRow.getCell(EX_DATE_NAME_COLUMN);
            nameCell.setCellValue( executorResponse.getFullName() );

            XSSFCell totalNormCell = executorRow.getCell(EX_TOTAL_NORM_COLUMN);
            totalNormCell.setCellValue( executorResponse.getTotalByNorm() );
            totalByNorm += executorResponse.getTotalByNorm();

            XSSFCell totalPriceCell = executorRow.getCell(EX_TOTAL_PRICE_COLUMN);
            totalPriceCell.setCellValue( executorResponse.getTotalByPrice() );
            totalByPrice += executorResponse.getTotalByPrice();

            XSSFCell totalCell = executorRow.getCell(EX_TOTAL_COLUMN);
            totalCell.setCellValue( executorResponse.getTotalSum() );
            totalSum += executorResponse.getTotalSum();

            XSSFCell percentCell = executorRow.getCell(EX_PERCENT_COLUMN);
            percentCell.setCellValue( executorResponse.getPercent() / 100.0 );

            XSSFCell salaryCell = executorRow.getCell(EX_SALARY_COLUMN);
            salaryCell.setCellValue( executorResponse.getSalary() );
            totalSalary += executorResponse.getSalary();

            executorRow.setHeight( (short) -1 );

            int documentCounter = 1;

            for ( ExecutorDocumentResponse executorDocumentResponse : executorResponse.getExecutorDocumentResponses() ) {

                XSSFRow documentRow = dataSheet.getRow( EX_BODY_ROW + counter++ );

                XSSFCell documentNameCell = documentRow.getCell(EX_DATE_NAME_COLUMN);
                documentNameCell.setCellValue( generateDocumentName( executorDocumentResponse.getDocumentDate(), documentCounter++ ) );

                XSSFCell documentTotalNormCell = documentRow.getCell(EX_TOTAL_NORM_COLUMN);
                documentTotalNormCell.setCellValue( executorDocumentResponse.getTotalByNorm() );

                XSSFCell documentTotalPriceCell = documentRow.getCell(EX_TOTAL_PRICE_COLUMN);
                documentTotalPriceCell.setCellValue( executorDocumentResponse.getTotalByPrice() );

                XSSFCell documentTotalCell = documentRow.getCell(EX_TOTAL_COLUMN);
                documentTotalCell.setCellValue( executorDocumentResponse.getTotalSum() );

                XSSFCell documentPercentCell = documentRow.getCell(EX_PERCENT_COLUMN);
                documentPercentCell.setCellValue( executorResponse.getPercent() / 100.0 );

                XSSFCell documentSalaryCell = documentRow.getCell(EX_SALARY_COLUMN);
                documentSalaryCell.setCellValue( executorDocumentResponse.getSalary() );

            }

        }

        XSSFRow totalRow = dataSheet.getRow( EX_TOTAL_ROW + bodySize - 1 );

        XSSFCell totalNormCell = totalRow.getCell(EX_TOTAL_NORM_COLUMN);
        totalNormCell.setCellValue( totalByNorm );

        XSSFCell totalPriceCell = totalRow.getCell(EX_TOTAL_PRICE_COLUMN);
        totalPriceCell.setCellValue( totalByPrice );

        XSSFCell totalCell = totalRow.getCell(EX_TOTAL_COLUMN);
        totalCell.setCellValue( totalSum );

        XSSFCell salaryCell = totalRow.getCell(EX_SALARY_COLUMN);
        salaryCell.setCellValue( totalSalary );

    }

    @Override
    public byte[] getClientsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, DataNotFoundException {

        File template = new File(reportsCatalog + "clients.xlsx");
        InputStream templateStream = new FileInputStream(template);

        XSSFWorkbook workBook = new XSSFWorkbook( templateStream );
        XSSFSheet dataSheet = workBook.getSheetAt(0);

        List<ClientResponse> clientResponses = getClientsResponses(organizationId, startDate, endDate);
        if ( clientResponses.size() == 0 ) throw new DataNotFoundException();

        int bodySize = clientResponses.stream()
                .mapToInt( value -> value.getClientDocumentResponses().size() + 1).sum();

        prepareTitle( dataSheet, startDate, endDate, "Отчет о реализации", CL_TITLE_ROW, CL_TITLE_COLUMN );
        prepareClientsBody( dataSheet, bodySize );
        fillClientsDocuments( dataSheet, clientResponses, bodySize );

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workBook.write(bos);
        return bos.toByteArray();
    }

    private void prepareClientsBody(XSSFSheet dataSheet, Integer bodySize) {
        if ( bodySize < 2 ) {
            logger.info("No need to prepare body, skipping...");
            return;
        }

        dataSheet.shiftRows(CL_TOTAL_ROW, CL_TOTAL_ROW, bodySize - 1);
        CellCopyPolicy policy = new CellCopyPolicy();

        for (int i = 1; i <= bodySize - 1; i++) {
            dataSheet.copyRows(CL_BODY_ROW, CL_BODY_ROW, CL_BODY_ROW + i, policy);
        }
    }

    private void fillClientsDocuments(XSSFSheet dataSheet, List<ClientResponse> clientResponses, Integer bodySize) {

        int counter = 0;

        double total = 0.0;

        for ( ClientResponse clientResponse : clientResponses ) {

            XSSFRow clientRow = dataSheet.getRow( CL_BODY_ROW + counter++ );

            XSSFCell nameCell = clientRow.getCell(CL_DATE_NAME_COLUMN);
            nameCell.setCellValue( clientResponse.getFullName() );

            XSSFCell totalCell = clientRow.getCell(CL_TOTAL_COLUMN);
            totalCell.setCellValue( clientResponse.getTotal() );
            total += clientResponse.getTotal();

            clientRow.setHeight( (short) -1 );

            int documentCounter = 1;

            for ( ClientDocumentResponse clientDocumentResponse : clientResponse.getClientDocumentResponses() ) {

                XSSFRow documentRow = dataSheet.getRow( CL_BODY_ROW + counter++ );

                XSSFCell documentNameCell = documentRow.getCell(CL_DATE_NAME_COLUMN);
                documentNameCell.setCellValue( generateDocumentName( clientDocumentResponse.getDocumentDate(), documentCounter++ ) );

                XSSFCell documentTotalCell = documentRow.getCell(CL_TOTAL_COLUMN);
                documentTotalCell.setCellValue( clientDocumentResponse.getTotal() );

            }

        }

        XSSFRow totalRow = dataSheet.getRow( CL_TOTAL_ROW + bodySize - 1 );

        XSSFCell totalCell = totalRow.getCell(CL_TOTAL_COLUMN);
        totalCell.setCellValue( total );

    }

    private String generateDocumentName(Date date, int counter) {
        return String.format("  №%s от %s", counter, DateHelper.formatDate( date ) );
    }

    private void prepareTitle(XSSFSheet dataSheet, Date startDate, Date endDate, String title, int titleRowNum, int titleColumnNum) {
        XSSFRow titleRow = dataSheet.getRow( titleRowNum );
        XSSFCell titleCell = titleRow.getCell( titleColumnNum );

        String titleMessage = String.format( "%s за период с %s по %s", title, DateHelper.formatDate( startDate ), DateHelper.formatDate( endDate ) );
        titleCell.setCellValue( titleMessage );
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
