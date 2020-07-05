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
@JsonApiResource(type = "serviceAddon", resourcePath = "serviceAddons")
@Where(clause = "deleted=false")
@Entity
public class ServiceAddon extends BaseEntity {

    private Integer count;
    private Double cost;
    private String name;
    private String number;
    private Boolean deleted;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private ServiceDocument document;

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("type", "З");
        reportData.put("name", name);
        reportData.put("priceNorm", count != null ? count.doubleValue() : 0.0);
        reportData.put("price", cost);
        reportData.put("sum", calculateServiceAddonTotalCost() );

        return reportData;
    }

    public Double calculateServiceAddonTotalCost() {

        double workSum = 0.0;
        int quantity = this.count != null && this.count > 0 ?
                this.count : 1;

        if ( cost != null ) {
            workSum += cost;
        }

        return workSum * quantity;
    }

}
