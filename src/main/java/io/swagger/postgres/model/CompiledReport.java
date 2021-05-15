package io.swagger.postgres.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class CompiledReport extends BaseEntity {

    private String fileName;
    private String uuid;
    private Date compileDate;

}
