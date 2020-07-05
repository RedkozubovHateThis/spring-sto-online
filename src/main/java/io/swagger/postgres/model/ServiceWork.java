package io.swagger.postgres.model;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(of = "id", callSuper = true)
@Data
@JsonApiResource(type = "serviceWork", resourcePath = "serviceWorks")
@Where(clause = "deleted=false")
@Entity
public class ServiceWork extends BaseEntity {

    private Integer count;
    private Double priceNorm;
    private Double price;
    private Boolean byPrice;
    private Double timeValue;
    private String name;
    private String number;
    private Boolean deleted;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private ServiceDocument document;

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("type", "Р");
        reportData.put("name", name);
        reportData.put("priceNorm", byPrice ? count : timeValue);
        reportData.put("price", byPrice ? price : priceNorm);
        reportData.put("sum", calculateServiceWorkTotalCost() );

        return reportData;
    }

    public Double calculateServiceWorkTotalCost() {

        double workSum = 0.0;
        int quantity = this.count != null && this.count > 0 ?
                this.count : 1;

        if ( !byPrice && priceNorm != null && timeValue != null ) {
            workSum += priceNorm * timeValue;
        }
        else if ( byPrice && price != null ) {
            workSum += price;
        }

        return workSum * quantity;
    }

}
