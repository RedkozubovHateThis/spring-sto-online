package io.swagger.response.integration.errors;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class IntegrationErrors {

    List<IntegrationError> errors;

    public IntegrationErrors(HttpStatus status, String detail, String path) {
        errors = new ArrayList<>();
        errors.add( new IntegrationError(status, detail, path) );
    }
}
