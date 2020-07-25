package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.ReportType;
import io.swagger.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/reports/")
@RestController
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportService reportService;

    @Value("${domain.demo}")
    private Boolean demoDomain;

    @GetMapping("{documentId}/{reportType}")
    public ResponseEntity getOrderResponse(@PathVariable("documentId") Long documentId,
                                           @PathVariable("reportType") ReportType reportType) {

        try {
            byte[] response;
            switch(reportType) {
                case ORDER: response = reportService.getOrderReport(documentId, "order.jasper"); break;
                case ORDER_ACT: response = reportService.getOrderReport(documentId, "orderAct.jasper"); break;
                case ORDER_PAYMENT: response = reportService.getOrderPaymentReport(documentId); break;
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
//
//    @GetMapping("/executors")
//    public ResponseEntity findExecutors(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
//                                        @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate,
//                                        @RequestParam(value = "organizationId", required = false) Integer organizationId) {
//
//        User currentUser = userRepository.findCurrentUser();
//        List<ExecutorResponse> responses;
//
//        if ( UserHelper.isServiceLeader( currentUser ) ) {
//            responses = reportService.getExecutorResponses(currentUser.getOrganizationId(), startDate, endDate);
//        }
//        else if ( UserHelper.isAdmin( currentUser ) ) {
//
//            if ( organizationId == null )
//                return ResponseEntity.status(404).build();
//
//            responses = reportService.getExecutorResponses(organizationId, startDate, endDate);
//        }
//        else
//            return ResponseEntity.status(403).build();
//
//        if ( demoDomain && responses.size() == 0 )
//            responses = reportService.getExecutorFakeResponses( startDate, endDate );
//
//        return ResponseEntity.ok(responses);
//
//    }
//
//    @GetMapping("/executors/PDF")
//    public ResponseEntity buildExecutorsReport(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
//                                               @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate,
//                                               @RequestParam(value = "organizationId", required = false) Integer organizationId) {
//
//        User currentUser = userRepository.findCurrentUser();
//
//        try {
//
//            byte[] response;
//
//            if ( UserHelper.isServiceLeader( currentUser ) ) {
//                response = reportService.getExecutorsReport(currentUser.getOrganizationId(), startDate, endDate);
//            }
//            else if ( UserHelper.isAdmin( currentUser ) ) {
//
//                if ( organizationId == null )
//                    return ResponseEntity.status(404).build();
//
//                response = reportService.getExecutorsReport(organizationId, startDate, endDate);
//            }
//            else
//                return ResponseEntity.status(403).build();
//
//            return ResponseEntity.ok()
//                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
//                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
//                    .contentLength( response.length )
//                    .body( response );
//        } catch (IOException | JRException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//        catch (DataNotFoundException dnfe) {
//            dnfe.printStackTrace();
//            return ResponseEntity.status(404).build();
//        }
//
//    }
//
//    @GetMapping("/clients")
//    public ResponseEntity findClients(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
//                                      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate,
//                                      @RequestParam(value = "organizationId", required = false) Integer organizationId) {
//
//        User currentUser = userRepository.findCurrentUser();
//        List<ClientResponse> responses;
//
//        if ( UserHelper.isServiceLeader( currentUser ) ) {
//            responses = reportService.getClientsResponses(currentUser.getOrganizationId(), startDate, endDate);
//        }
//        else if ( UserHelper.isAdmin( currentUser ) ) {
//
//            if ( organizationId == null )
//                return ResponseEntity.status(404).build();
//
//            responses = reportService.getClientsResponses(organizationId, startDate, endDate);
//        }
//        else
//            return ResponseEntity.status(403).build();
//
//        if ( demoDomain && responses.size() == 0 )
//            responses = reportService.getClientFakeResponses( startDate, endDate );
//
//        return ResponseEntity.ok( responses );
//
//    }
//
//    @GetMapping("/clients/PDF")
//    public ResponseEntity buildClientsReport(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
//                                             @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate,
//                                             @RequestParam(value = "organizationId", required = false) Integer organizationId) {
//
//        User currentUser = userRepository.findCurrentUser();
//
//        try {
//
//            byte[] response;
//
//            if ( UserHelper.isServiceLeader( currentUser ) ) {
//                response = reportService.getClientsReport(currentUser.getOrganizationId(), startDate, endDate);
//            }
//            else if ( UserHelper.isAdmin( currentUser ) ) {
//
//                if ( organizationId == null )
//                    return ResponseEntity.status(404).build();
//
//                response = reportService.getClientsReport(organizationId, startDate, endDate);
//            }
//            else
//                return ResponseEntity.status(403).build();
//
//            return ResponseEntity.ok()
//                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
//                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
//                    .contentLength( response.length )
//                    .body( response );
//        } catch (IOException | JRException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).build();
//        }
//        catch (DataNotFoundException dnfe) {
//            dnfe.printStackTrace();
//            return ResponseEntity.status(404).build();
//        }
//
//    }

}
