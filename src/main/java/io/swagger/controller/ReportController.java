package io.swagger.controller;

import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.security.User;
import io.swagger.postgres.repository.UserRepository;
import io.swagger.response.exception.DataNotFoundException;
import io.swagger.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RequestMapping("/secured/reports/")
@RestController
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping("/executors")
    public ResponseEntity findExecutors(@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss") Date startDate,
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
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        catch (DataNotFoundException dnfe) {
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

        try {
            byte[] response = reportService.getClientsReport(currentUser.getOrganizationId(), startDate, endDate);

            return ResponseEntity.ok()
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
                    .contentLength( response.length )
                    .body( response );
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        catch (DataNotFoundException dnfe) {
            return ResponseEntity.status(404).build();
        }

    }

}
