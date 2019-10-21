package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.ReportType;
import io.swagger.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

@RequestMapping("/secured/reports/")
@RestController
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping("{documentId}/{reportType}")
    public ResponseEntity getOrderResponse(@PathVariable("documentId") Integer documentId,
                                           @PathVariable("reportType") ReportType reportType) {

        try {
            byte[] response;
            switch (reportType) {
                case ORDER: response = reportService.getOrderReport(documentId); break;
                case ORDER_ACT: response = reportService.getOrderActReport(documentId); break;
                case ORDER_DEFECTION: response = reportService.getOrderDefectionReport(documentId); break;
                case ORDER_INSPECTION: response = reportService.getOrderInspectionReport(documentId); break;
                case ORDER_RECEIPT: response = reportService.getOrderReceiptReport(documentId); break;
                case ORDER_REQUEST: response = reportService.getOrderRequestReport(documentId); break;
                case ORDER_REQUIREMENT: response = reportService.getOrderRequirementReport(documentId); break;
                case ORDER_TASK: response = reportService.getOrderTaskReport(documentId); break;
                case ORDER_TRANSFER: response = reportService.getOrderTransferReport(documentId); break;
                default: return ResponseEntity.status(400).build();
            }

            return ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
                    .contentLength( response.length )
                    .body( response );
        } catch (IOException | JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        catch (DataNotFoundException dnfe) {
            dnfe.printStackTrace();
            return ResponseEntity.status(404).build();
        }

    }

    @GetMapping("/executors")
    public ResponseEntity findExecutors(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
                                        @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) )
            return ResponseEntity.status(403).build();

        if ( currentUser.getOrganizationId() == null )
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok( reportService.getExecutorResponses( currentUser.getOrganizationId(), startDate, endDate ) );
//        return ResponseEntity.ok( reportService.getExecutorFakeResponses( startDate, endDate ) );

    }

    @GetMapping("/executors/PDF")
    public ResponseEntity buildExecutorsReport(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
                                               @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) )
            return ResponseEntity.status(403).build();

        if ( currentUser.getOrganizationId() == null )
            return ResponseEntity.status(404).build();

        try {
            byte[] response = reportService.getExecutorsReport(currentUser.getOrganizationId(), startDate, endDate);

            return ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
                    .contentLength( response.length )
                    .body( response );
        } catch (IOException | JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        catch (DataNotFoundException dnfe) {
            dnfe.printStackTrace();
            return ResponseEntity.status(404).build();
        }

    }

    @GetMapping("/clients")
    public ResponseEntity findClients(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
                                      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) )
            return ResponseEntity.status(403).build();

        if ( currentUser.getOrganizationId() == null || !currentUser.getIsApproved() )
            return ResponseEntity.status(404).build();

//        return ResponseEntity.ok( reportService.getClientFakeResponses( startDate, endDate ) );
        return ResponseEntity.ok( reportService.getClientsResponses( currentUser.getOrganizationId(), startDate, endDate ) );

    }

    @GetMapping("/clients/PDF")
    public ResponseEntity buildClientsReport(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
                                             @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate) {

        User currentUser = userRepository.findCurrentUser();
        if ( !UserHelper.hasRole( currentUser, "SERVICE_LEADER" ) )
            return ResponseEntity.status(403).build();

        if ( currentUser.getOrganizationId() == null )
            return ResponseEntity.status(404).build();

        try {
            byte[] response = reportService.getClientsReport(currentUser.getOrganizationId(), startDate, endDate);

            return ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
                    .contentLength( response.length )
                    .body( response );
        } catch (IOException | JRException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        catch (DataNotFoundException dnfe) {
            dnfe.printStackTrace();
            return ResponseEntity.status(404).build();
        }

    }

}
