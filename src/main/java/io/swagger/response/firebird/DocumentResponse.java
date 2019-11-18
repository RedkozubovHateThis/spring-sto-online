package io.swagger.response.firebird;

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
    private Double sum = 0.0;
    private Integer userId;
    private String userFio;

    private String reason;
    private String organization;

    private ServiceWorkTotalResponse serviceWork;
    private ServiceGoodsAddonTotalResponse serviceGoodsAddon;
    private GoodsOutClientTotalResponse goodsOutClient;

    public DocumentResponse(DocumentServiceDetail document) {

        if ( document == null ) return;

        id = document.getId();
        startDate = DateHelper.formatDateTime( document.getDateStart() );
//        sum = document.getSummaWork();
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
        endDate = DateHelper.formatDateTime( documentOutHeader.getDateCreate() );

        User user = documentOutHeader.getUser();
        if ( user != null ) {
            Employee employee = user.getEmployee();

            if ( employee != null ) {
                userId = user.getId();
                userFio = employee.getShortName();
            }

        }

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) return;

        serviceWork = new ServiceWorkTotalResponse(documentOut, this);
        serviceGoodsAddon = new ServiceGoodsAddonTotalResponse(documentOut, this);
        goodsOutClient = new GoodsOutClientTotalResponse(documentOut);

        Client client = documentOut.getClient();
        if ( client != null ) this.client = client.getShortName();

        Organization organization = documentOut.getOrganization();
        if ( organization != null ) this.organization = organization.getFullName();

    }

    public void incrementSum(Double sum) {
        this.sum += sum;
    }

}
