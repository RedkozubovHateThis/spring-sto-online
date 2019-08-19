package io.swagger.response;

import io.swagger.firebird.model.*;
import io.swagger.helper.DateHelper;
import lombok.Data;

@Data
public class FirebirdResponse {

    private String startDate;
    private String endDate;
    private String state;
    private String style;
    private String client;
    private String vehicle;
    private Double sum;

    //TODO: переделать все это говно на hateoas, и запрашивать данные сразу с базы в режиме проксирования
    public FirebirdResponse(DocumentServiceDetail document) {

        if ( document == null ) return;

        startDate = DateHelper.formatDate( document.getDateStart() );
        sum = document.getSummaWork();

        ModelLink modelLink = document.getModelLink();
        if ( modelLink != null ) {

            ModelDetail modelDetail = modelLink.getModelDetail();

            if ( modelDetail != null ) {

                Model model = modelDetail.getModel();

                if ( model != null ) {

                    Mark mark = model.getMark();

                    if ( mark != null )
                        vehicle = mark.getName() + " " + model.getName();
                    else
                        vehicle = model.getName();

                }

            }

        }

        DocumentOutHeader documentOutHeader = document.getDocumentOutHeader();
        if ( documentOutHeader == null ) return;

        state = documentOutHeader.getState() == 4 ? "Оформлен" : documentOutHeader.getState() == 2 ? "Черновик" : "Неизвестно";
        style = documentOutHeader.getState() == 4 ? "success" : documentOutHeader.getState() == 2 ? "warning" : "info";

        DocumentOut documentOut = documentOutHeader.getDocumentOut();
        if ( documentOut == null ) return;

        Client client = documentOut.getClient();
        if ( client != null ) this.client = client.getShortName();

    }

}
