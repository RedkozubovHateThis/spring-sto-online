package io.swagger.postgres.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id", callSuper = true)
@Data
@JsonApiResource(type = "adEntity", resourcePath = "adEntities")
@Where(clause = "deleted=false")
@Entity
public class AdEntity extends BaseEntity implements Serializable {

    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String phone;
    private String email;
    private String url;
    private Date createDate;
    private Boolean current;
    private Boolean sideOffer;
    private Boolean active;
    private Boolean deleted;

    @OneToOne(mappedBy = "adEntity")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private User serviceLeader;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private User sideOfferServiceLeader;
}
