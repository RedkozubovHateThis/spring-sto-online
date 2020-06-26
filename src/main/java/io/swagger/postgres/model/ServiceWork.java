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

}
