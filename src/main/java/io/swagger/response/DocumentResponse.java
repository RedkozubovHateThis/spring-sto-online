package io.swagger.response;

import io.swagger.firebird.model.*;
import io.swagger.helper.DateHelper;
import lombok.Data;

@Data
public class DocumentResponse {

    private Integer id;
    private Integer documentOutHeaderId;
    private String documentNumber;
    private String startDate;
    private String endDate;
    private Integer state;
    private String stateRus;
    private String style;
    private String client;
    private VehicleResponse vehicle;
    private Double sum;

    private String reason;
    private String organization;

    private ServiceWorkTotalResponse serviceWork;

    public DocumentResponse(DocumentServiceDetail document) {

        if ( document == null ) return;

        id = document.getId();
        startDate = DateHelper.formatDate( document.getDateStart() );
        sum = document.getSummaWork();
        reason = document.getReasonsAppeal();

        ModelLink modelLink = document.getModelLink();
        if ( modelLink != null ) vehicle = new VehicleResponse(modelLink);

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) return;

        documentOutHeaderId = documentOutHeader.getId();
        documentNumber = documentOutHeader.getFullNumber();
        state = documentOutHeader.getState();
        stateRus = documentOutHeader.getState() == 4 ? "Оформлен" : documentOutHeader.getState() == 2 ? "Черновик" : "Неизвестно";
        style = documentOutHeader.getState() == 4 ? "success" : documentOutHeader.getState() == 2 ? "warning" : "info";

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) return;

        serviceWork = new ServiceWorkTotalResponse(documentOut);

        Client client = documentOut.getClient();
        if ( client != null ) this.client = client.getShortName();

        Organization organization = documentOut.getOrganization();
        if ( organization != null ) this.organization = organization.getFullName();

    }

}