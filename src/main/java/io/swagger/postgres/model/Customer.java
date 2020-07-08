package io.swagger.postgres.model;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.Serializable;

@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Data
@JsonApiResource(type = "customer", resourcePath = "customers", deletable = false)
public class Customer extends BaseEntity implements Serializable {

    private String name;
    private String address;
    private String email;
    private String phone;
    private String inn;
    private Boolean deleted;

}
