package io.swagger.response.firebird;

import io.swagger.firebird.model.Mark;
import io.swagger.firebird.model.Model;
import io.swagger.firebird.model.ModelDetail;
import io.swagger.firebird.model.ModelLink;
import lombok.Data;

@Data
public class VehicleResponse {

    private Integer id;
    private String name;
    private String regNumber;
    private String vinNumber;

    public VehicleResponse(ModelLink modelLink) {

        if ( modelLink == null ) return;

        id = modelLink.getId();

        ModelDetail modelDetail = modelLink.getModelDetail();

        if ( modelDetail != null ) {

            regNumber = modelDetail.getNormalizedRegNumber();
            vinNumber = modelDetail.getVinNumber();

            Model model = modelDetail.getModel();

            if ( model != null ) {

                Mark mark = model.getMark();

                if ( mark != null )
                    name = mark.getName() + " " + model.getName();
                else
                    name = model.getName();

            }

        }

    }

    public VehicleResponse(Model model) {

        if ( model == null ) return;

        id = model.getId();

        Mark mark = model.getMark();

        if ( mark != null )
            name = mark.getName() + " " + model.getName();
        else
            name = model.getName();

    }

}
