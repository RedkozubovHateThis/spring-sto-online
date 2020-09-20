package io.swagger.response.integration.errors;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class IntegrationError {

    private String status;
    private String title;
    private String detail;
    private IntegrationMeta meta;

    public IntegrationError(HttpStatus status, String detail, String path) {
        this.status = String.valueOf(status.value());
        this.title = status.getReasonPhrase();
        this.detail = detail;
        meta = new IntegrationMeta(path);
    }

}
