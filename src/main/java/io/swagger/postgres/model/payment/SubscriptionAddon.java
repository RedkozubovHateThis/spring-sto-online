package io.swagger.postgres.model.payment;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class SubscriptionAddon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Integer documentsCount;

    @Column(nullable = false)
    private Double cost;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private Subscription subscription;

    @OneToOne(mappedBy = "subscriptionAddon")
    @NotFound(action = NotFoundAction.IGNORE)
    private PaymentRecord paymentRecord;

    public SubscriptionAddon() {}
}
