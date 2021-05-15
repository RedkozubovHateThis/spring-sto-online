package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.ClientResponse;
import io.swagger.response.report.ExecutorResponse;
import io.swagger.response.report.ReportType;
import io.swagger.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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

    @GetMapping("/clients")
    public ResponseEntity findClients(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") @RequestParam(value = "startDate", required = false) Date startDate,
                                      @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") @RequestParam(value = "endDate", required = false) Date endDate,
                                      @RequestParam(value = "organizationId", required = false) Long organizationId) {

        User currentUser = userRepository.findCurrentUser();
        List<ClientResponse> responses;

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null ) {
            responses = reportService.getClientsResponses(currentUser.getProfile().getId(), startDate, endDate);
        }
        else if ( UserHelper.isAdmin( currentUser ) ) {

            if ( organizationId == null )
                return ResponseEntity.status(404).build();

            responses = reportService.getClientsResponses(organizationId, startDate, endDate);
        }
        else
            return ResponseEntity.status(403).build();

//        if ( demoDomain && responses.size() == 0 )
//            responses = reportService.getClientFakeResponses( startDate, endDate );

        return ResponseEntity.ok( responses );

    }

    @GetMapping("/clients/PDF")
    public ResponseEntity buildClientsReport(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
                                             @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate,
                                             @RequestParam(value = "organizationId", required = false) Long organizationId) {

        User currentUser = userRepository.findCurrentUser();

        try {

            byte[] response;

            if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null ) {
                response = reportService.getClientsReport(currentUser.getProfile().getId(), startDate, endDate);
            }
            else if ( UserHelper.isAdmin( currentUser ) ) {

                if ( organizationId == null )
                    return ResponseEntity.status(404).build();

                response = reportService.getClientsReport(organizationId, startDate, endDate);
            }
            else
                return ResponseEntity.status(403).build();

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

    @GetMapping("/vehicles")
    public ResponseEntity findVehicles(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") @RequestParam(value = "startDate", required = false) Date startDate,
                                       @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") @RequestParam(value = "endDate", required = false) Date endDate,
                                       @RequestParam(value = "vinNumber") String vinNumber,
                                       @RequestParam(value = "organizationId", required = false) Long organizationId) {

        User currentUser = userRepository.findCurrentUser();
        List<ClientResponse> responses;

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null ) {
            responses = reportService.getVehiclesResponses(currentUser.getProfile().getId(), vinNumber, startDate, endDate);
        }
        else if ( UserHelper.isAdmin( currentUser ) ) {

            if ( organizationId == null )
                return ResponseEntity.status(404).build();

            responses = reportService.getVehiclesResponses(organizationId, vinNumber, startDate, endDate);
        }
        else
            return ResponseEntity.status(403).build();

//        if ( demoDomain && responses.size() == 0 )
//            responses = reportService.getClientFakeResponses( startDate, endDate );

        return ResponseEntity.ok( responses );

    }

    @GetMapping("/vehicles/PDF")
    public ResponseEntity buildVehiclesReport(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
                                              @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date endDate,
                                              @RequestParam(value = "vinNumber") String vinNumber,
                                              @RequestParam(value = "organizationId", required = false) Long organizationId) {

        User currentUser = userRepository.findCurrentUser();

        try {

            byte[] response;

            if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null ) {
                response = reportService.getVehiclesReport(currentUser.getProfile().getId(), vinNumber, startDate, endDate);
            }
            else if ( UserHelper.isAdmin( currentUser ) ) {

                if ( organizationId == null )
                    return ResponseEntity.status(404).build();

                response = reportService.getVehiclesReport(organizationId, vinNumber, startDate, endDate);
            }
            else
                return ResponseEntity.status(403).build();

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

    @GetMapping("/registered")
    public ResponseEntity findRegistered(@RequestParam(value = "organizationId", required = false) Long organizationId) {

        User currentUser = userRepository.findCurrentUser();
        List<ExecutorResponse> responses;

        if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null ) {
            responses = reportService.getRegisteredResponses(currentUser.getProfile().getId());
        }
        else if ( UserHelper.isAdmin( currentUser ) ) {
            responses = reportService.getRegisteredResponses(organizationId);
        }
        else
            return ResponseEntity.status(403).build();

//        if ( demoDomain && responses.size() == 0 )
//            responses = reportService.getClientFakeResponses( startDate, endDate );

        return ResponseEntity.ok( responses );

    }

    @GetMapping("/registered/PDF")
    public ResponseEntity buildRegisteredReport(@RequestParam(value = "organizationId", required = false) Long organizationId) {

        User currentUser = userRepository.findCurrentUser();

        try {

            byte[] response;

            if ( UserHelper.isServiceLeaderOrFreelancer( currentUser ) && currentUser.getProfile() != null ) {
                response = reportService.getRegisteredReport(currentUser.getProfile().getId());
            }
            else if ( UserHelper.isAdmin( currentUser ) ) {
                response = reportService.getRegisteredReport(organizationId);
            }
            else
                return ResponseEntity.status(403).build();

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
