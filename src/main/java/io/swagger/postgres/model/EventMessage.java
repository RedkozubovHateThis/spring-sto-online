package io.swagger.postgres.model;

import io.swagger.postgres.model.enums.MessageType;
import io.swagger.postgres.model.security.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class EventMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User sendUser;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User targetUser;

    private Integer documentId;
    private String documentName;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT")
    private String additionalInformation;

    private Date messageDate;

}
