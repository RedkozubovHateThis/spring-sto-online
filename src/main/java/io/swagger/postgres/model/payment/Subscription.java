package io.swagger.postgres.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Subscription implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "subscription_type_id")
    private SubscriptionType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date startDate;
    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private Boolean isRenewable;

    @Column(nullable = false)
    private Double documentCost;
    @Column(nullable = false)
    private Integer documentsCount;

    @OneToOne(mappedBy = "currentSubscription")
    @NotFound(action = NotFoundAction.IGNORE)
    private User asCurrentUser;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User user;

    @OneToOne(mappedBy = "subscription")
    @NotFound(action = NotFoundAction.IGNORE)
    private PaymentRecord paymentRecord;

    @OneToMany(mappedBy = "subscription")
    private Set<SubscriptionAddon> addons = new HashSet<>();

    private Boolean isClosedEarly;

    public Subscription() {}

    public Subscription(SubscriptionType subscriptionType) {
        this.name = subscriptionType.getName();
        this.type = subscriptionType;
        this.isRenewable = !subscriptionType.getIsFree();
        this.documentCost = subscriptionType.getDocumentCost();
        this.documentsCount = subscriptionType.getDocumentsCount();
    }

    public void applyAddon(SubscriptionAddon addon) {
        this.documentsCount += addon.getDocumentsCount();
    }
}
