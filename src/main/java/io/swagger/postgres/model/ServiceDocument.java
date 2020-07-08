package io.swagger.postgres.model;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.enums.ServiceDocumentPaidStatus;
import io.swagger.postgres.model.enums.ServiceDocumentStatus;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id", callSuper = true)
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
    @Enumerated(EnumType.STRING)
    private ServiceDocumentPaidStatus paidStatus;
    private Boolean deleted;
    private Double cost;
    @Column(columnDefinition = "TEXT")
    private String reason;
    private Boolean clientIsCustomer;
    private String masterFio;

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
    private Profile executor;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Profile client;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Vehicle vehicle;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private VehicleMileage vehicleMileage;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Customer customer;
}
