package io.swagger.response.integration.errors;

import io.swagger.helper.DateHelper;
import lombok.Data;

import java.util.Date;

@Data
public class IntegrationMeta {

    private String path;
    private String timestamp;

    public IntegrationMeta(String path) {
        this.path = path;
        this.timestamp = DateHelper.formatISO( new Date() );
    }

}
