package io.swagger.postgres.model.payment;

import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.postgres.model.BaseEntity;
import io.swagger.postgres.model.enums.SubscriptionOption;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Data
@JsonApiResource(type = "subscriptionType", resourcePath = "subscriptionTypes", postable = false, deletable = false)
public class SubscriptionType extends BaseEntity implements Serializable {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionOption subscriptionOption;
    @Column(nullable = false)
    private Integer sortPosition;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double cost;
    @Column(nullable = false)
    private Integer durationDays;

    @OneToMany(mappedBy = "type")
    @JsonApiRelation(mappedBy = "type", serialize = SerializeType.LAZY)
    private Set<Subscription> subscriptions = new HashSet<>();

}
