package io.swagger.postgres.model;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.security.Profile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonApiResource(type = "vehicle", resourcePath = "vehicles")
@Where(clause = "deleted=false")
@Entity
public class Vehicle extends BaseEntity {

    private String modelName;
    private String vinNumber;
    private String regNumber;
    private Integer year;
    private Boolean deleted;

    @OneToMany(mappedBy = "vehicle")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private Set<VehicleMileage> vehicleMileages = new HashSet<>();

    @OneToMany(mappedBy = "vehicle")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private Set<ServiceDocument> documents = new HashSet<>();

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Profile createdBy;
}
