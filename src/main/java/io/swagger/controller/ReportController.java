package io.swagger.controller;

import io.swagger.postgres.repository.UserRepository;
import io.swagger.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/reports/")
@RestController
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportService reportService;

    @Value("${domain.demo}")
    private Boolean demoDomain;

//    @GetMapping("{documentId}/{reportType}/{printStamp}")
//    public ResponseEntity getOrderResponse(@PathVariable("documentId") Integer documentId,
//                                           @PathVariable("printStamp") Boolean printStamp,
//                                           @PathVariable("reportType") ReportType reportType) {
//
//        User currentUser = userRepository.findCurrentUser();
//        if ( UserHelper.isClient( currentUser ) ) {
//
//            if ( !reportType.equals( ReportType.ORDER ) && !reportType.equals( ReportType.ORDER_ACT )
//                    && !reportType.equals( ReportType.ORDER_TRANSFER ) )
//                return ResponseEntity.status(403).build();
//
//            printStamp = true;
//
//        }
//
//        try {
//            byte[] response;
//            switch (reportType) {
//                case ORDER: response = reportService.getOrderReport(documentId, printStamp, false); break;
//                case ORDER_ACT: response = reportService.getOrderActReport(documentId, printStamp); break;
//                case ORDER_DEFECTION: response = reportService.getOrderDefectionReport(documentId); break;
//                case ORDER_INSPECTION: response = reportService.getOrderInspectionReport(documentId); break;
//                case ORDER_RECEIPT: response = reportService.getOrderReceiptReport(documentId); break;
//                case ORDER_REQUEST: response = reportService.getOrderRequestReport(documentId); break;
//                case ORDER_REQUIREMENT: response = reportService.getOrderRequirementReport(documentId); break;
//                case ORDER_TASK: response = reportService.getOrderTaskReport(documentId); break;
//                case ORDER_TRANSFER: response = reportService.getOrderTransferReport(documentId, printStamp); break;
//                default: return ResponseEntity.status(400).build();
//            }
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
