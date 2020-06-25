package io.swagger.postgres.model;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonApiResource(type = "vehicleMileage", resourcePath = "vehicleMileages")
@Where(clause = "deleted=false")
@Entity
public class VehicleMileage extends BaseEntity {

    private Integer mileage;
    private Boolean deleted;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private Vehicle vehicle;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private ServiceDocument document;
}
