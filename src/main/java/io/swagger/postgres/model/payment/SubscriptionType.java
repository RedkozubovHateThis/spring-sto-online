package io.swagger.postgres.model.payment;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@JsonApiResource(type = "subscriptionType", resourcePath = "subscriptionTypes")
public class SubscriptionType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonApiId
    private Long id;

    @Column(nullable = false)
    private Integer sortPosition;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean isFree;
    @Column(nullable = false)
    private Double cost;
    @Column(nullable = false)
    private Double documentCost;
    @Column(nullable = false)
    private Integer documentsCount;
    @Column(nullable = false)
    private Integer durationDays;

    @OneToMany(mappedBy = "type")
    @JsonApiRelation(mappedBy = "type", serialize = SerializeType.LAZY)
//    @JsonIgnore
    private Set<Subscription> subscriptions;

}
