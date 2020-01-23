package io.swagger.postgres.model.payment;

import io.swagger.postgres.model.security.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class SubscriptionType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Set<Subscription> subscriptions;

}
