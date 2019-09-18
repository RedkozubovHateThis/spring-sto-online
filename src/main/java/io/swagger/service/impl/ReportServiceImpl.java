package io.swagger.service.impl;

import io.swagger.firebird.repository.DocumentServiceDetailRepository;
import io.swagger.helper.DateHelper;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.*;
import io.swagger.service.ReportService;
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

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private DocumentServiceDetailRepository documentServiceDetailRepository;

    @Value("${reports.catalog}")
    private String reportsCatalog;

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
                documentSalaryCell.setCellValue( executorDocumentResponse.getSalary( executorResponse.getPercent() ) );

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

        List<ClientResponse> clientResponses = getClientsResponse(organizationId, startDate, endDate);
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


    private List<ExecutorResponse> getExecutorResponses(Integer organizationId, Date startDate, Date endDate) {

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

    private ExecutorResponse filterExecutorResponses(List<ExecutorResponse> executorResponses, ExecutorsNativeResponse response) {

        if ( executorResponses.size() == 0 ) return null;

        return executorResponses.stream()
                .filter( executorResponse -> executorResponse.getFullName().equals( response.getFullName() ) )
                .findAny().orElse( null );

    }

    private List<ClientResponse> getClientsResponse(Integer organizationId, Date startDate, Date endDate) {

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
