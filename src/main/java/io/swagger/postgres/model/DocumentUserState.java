package io.swagger.postgres.model;

import io.swagger.postgres.model.security.User;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class DocumentUserState implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Type(type ="io.swagger.config.database.GenericArrayUserType")
    private Integer[] documents;

    private Date lastUpdateDate;

    private Integer clientId;

}
