package io.swagger.postgres.model;

import io.swagger.postgres.model.security.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User fromUser;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User toUser;

    private Date messageDate;
    @Column(columnDefinition = "TEXT")
    private String messageText;

    @OneToOne
    private UploadFile uploadFile;

}
