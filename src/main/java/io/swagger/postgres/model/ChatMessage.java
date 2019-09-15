package io.swagger.postgres.model;

import io.swagger.postgres.model.security.User;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User fromUser;
    @ManyToOne
    private User toUser;

    private Date messageDate;
    @Column(columnDefinition = "TEXT")
    private String messageText;

    @OneToOne
    private UploadFile uploadFile;

}
