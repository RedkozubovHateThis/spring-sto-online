package io.swagger.postgres.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;

@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Data
@JsonApiResource(type = "profile", resourcePath = "profiles")
@Where(clause = "deleted=false")
public class Profile extends BaseEntity implements Serializable {

    @JsonIgnore
    private String integrationId;

    private String name;
    private String address;
    private String email;
    private String phone;
    private String inn;
    private Boolean deleted;

    @Transient
    private Boolean autoRegister;
    @Transient
    private Boolean byFio;
    @Transient
    private String firstName;
    @Transient
    private String lastName;
    @Transient
    private String middleName;

    @OneToOne(mappedBy = "profile")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private User user;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Profile createdBy;

}
