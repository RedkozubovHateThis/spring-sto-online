package io.swagger.controller;

import io.swagger.model.DocumentAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller    // This means that this class is a Controller
@RequestMapping(path = "/document_action") // This means URL's start with /demo (after Application path)
public class DocumentActionController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping(path = "/all")
    public ResponseEntity getAllDocumentAction() {
        List<DocumentAction> results = jdbcTemplate.query("SELECT * FROM DOCUMENT_ACTION",

                (rs, rowNum) -> new DocumentAction(rs.getInt("DOCUMENT_ACTION_ID"),
                        rs.getInt("DOCUMENT_REGISTRY_ID"),
                        rs.getInt("USER_ID"), rs.getDate("ACTION_DATETIME"),
                        rs.getInt("STATE"), rs.getDouble("SUMMA")));

        return ResponseEntity.ok(results);
    }
}
