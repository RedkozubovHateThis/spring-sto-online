package io.swagger.postgres.model;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonApiResource(type = "serviceDocument", resourcePath = "serviceDocuments")
@Where(clause = "deleted=false")
@Entity
public class ServiceDocument extends BaseEntity {

    private String number;
    private Date startDate;
    private Date endDate;
    @Enumerated(EnumType.STRING)
    private ServiceDocumentStatus status;
    private Boolean deleted;
    private Double cost;

    @OneToMany(mappedBy = "document")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private Set<ServiceWork> serviceWorks = new HashSet<>();

    @OneToMany(mappedBy = "document")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private Set<ServiceAddon> serviceAddons = new HashSet<>();

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private User executor;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private User client;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Vehicle vehicle;

    @OneToOne(mappedBy = "document")
    private VehicleMileage vehicleMileage;


}
