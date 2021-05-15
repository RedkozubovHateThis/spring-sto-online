package io.swagger.postgres.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.MappedSuperclass;

@Data
@EqualsAndHashCode(of = "id", callSuper = true)
@MappedSuperclass
public abstract class BaseDictionaryEntity extends BaseEntity {

    private String name;
    private Boolean deleted;
    private Boolean isInitial;

}
