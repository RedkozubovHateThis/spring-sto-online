package io.swagger.postgres.model;

import io.swagger.postgres.model.security.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String contentType;
    private Long size;
    private String uuid;
    private Date uploadDate;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User uploadUser;
    @OneToOne(mappedBy = "uploadFile")
    private ChatMessage chatMessage;

}
