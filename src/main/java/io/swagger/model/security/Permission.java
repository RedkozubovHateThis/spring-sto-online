package io.swagger.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "PERMISSION", uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
@Immutable
@Getter
@Setter
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    @OrderBy
    private Set<Role> roles;

    public String getName() {
        return name;
    }
}

