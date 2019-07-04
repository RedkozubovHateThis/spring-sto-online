package io.swagger.api;

import io.swagger.model.DocOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
@Controller
public class DocOrderApiController implements DocOrderApi {

    private static final Logger log = LoggerFactory.getLogger(DocOrderApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public DocOrderApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> createTaskVisit(@ApiParam(value = "Создание наряда" ,required=true )  @Valid @RequestBody DocOrder body) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> docOrderDelete(@ApiParam(value = "ID наряда, который необходимо удалить") @Valid @RequestParam(value = "id", required = false) Integer id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<DocOrder>> docOrderGet(@ApiParam(value = "id наряда") @Valid @RequestParam(value = "id", required = false) Integer id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<List<DocOrder>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> docOrderPut(@ApiParam(value = "Обновление данных о наряде" ,required=true )  @Valid @RequestBody DocOrder body,@ApiParam(value = "ид задчи на посещение магазина, которую необходимо обновить") @Valid @RequestParam(value = "id", required = false) Integer id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

}
