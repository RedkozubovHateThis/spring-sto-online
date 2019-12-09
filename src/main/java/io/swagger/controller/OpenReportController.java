package io.swagger.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/open/report")
public class OpenReportController {

    @Value("${compiled.catalog}")
    private String compiledReportCatalog;

    @GetMapping("/compiled")
    public ResponseEntity getCompiledReport(@RequestParam("uuid") String uuid) {

        File reportFile = new File( String.format( "%s%s", compiledReportCatalog, uuid ) );
        if ( !reportFile.exists() ) return ResponseEntity.status(404).build();

        byte[] response;

        try {
            response = Files.readAllBytes( reportFile.toPath() );
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok()
                .header( HttpHeaders.CONTENT_DISPOSITION, "attachment" )
                .contentType( MediaType.APPLICATION_OCTET_STREAM )
                .contentLength( response.length )
                .body( response );
    }

}
