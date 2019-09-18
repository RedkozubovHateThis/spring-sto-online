package io.swagger.response.report;

import lombok.Data;

import java.util.Date;

@Data
public class ClientDocumentResponse {

    private Date documentDate;
    private Double total;

    public ClientDocumentResponse(ClientsNativeResponse response) {
        this.documentDate = response.getDateStart();
        this.total = response.getTotal();
    }

}
