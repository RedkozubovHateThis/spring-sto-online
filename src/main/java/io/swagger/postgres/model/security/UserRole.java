package io.swagger.postgres.model.security;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.swagger.postgres.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode(of = "id", callSuper = true)
@JsonApiResource(type = "userRole", resourcePath = "userRoles", deletable = false, postable = false, patchable = false)
public class UserRole extends BaseEntity implements Serializable {

    @Column(unique = true)
    private String name;
    private String nameRus;

}

