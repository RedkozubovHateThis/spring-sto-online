package io.swagger.postgres.model.payment;

import io.swagger.postgres.model.security.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date startDate;
    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private Boolean isRenewable;
    @Column(nullable = false)
    private Double renewalCost;

    @Column(nullable = false)
    private Double documentCost;
    @Column(nullable = false)
    private Integer documentsCount;

    @OneToOne(mappedBy = "currentSubscription")
    @NotFound(action = NotFoundAction.IGNORE)
    private User user;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User asPreviousUser;
}
