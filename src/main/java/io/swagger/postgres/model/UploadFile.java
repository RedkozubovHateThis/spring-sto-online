package io.swagger.postgres.model;

import io.swagger.postgres.model.security.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(of = "id", callSuper = true)
@Entity
@Data
public class UploadFile extends BaseEntity {

    private String fileName;
    private String contentType;
    private Long size;
    private String uuid;
    private Date uploadDate;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User uploadUser;

}
