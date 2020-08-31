package io.swagger.postgres.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.BaseEntity;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Data
@JsonApiResource(type = "subscription", resourcePath = "subscriptions", postable = false, deletable = false, patchable = false)
public class Subscription extends BaseEntity implements Serializable {

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "subscription_type_id")
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private SubscriptionType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date startDate;
    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private Boolean isRenewable;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation
    private User user;

    @OneToOne(mappedBy = "subscription")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private PaymentRecord paymentRecord;

    @OneToMany(mappedBy = "subscription")
    @JsonIgnore
    private Set<SubscriptionAddon> addons = new HashSet<>();

    public Subscription() {}

    public Subscription(SubscriptionType subscriptionType) {
        this.name = subscriptionType.getName();
        this.type = subscriptionType;
        this.isRenewable = true;
    }
}
