package io.swagger.firebird.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
@Data
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_USERS")
            })
    @Column(name = "USER_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;

    @Column(name = "HIDDEN")
    private Short hidden;

    @JsonIgnore
    @Lob
    @Column(name = "USER_RIGHT")
    private String userRight;

    @Column(name = "USER_LOCK")
    private Short userLock;
    @Column(name = "FORCE_LOGOUT")
    private Short forceLogout;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<DocumentOutHeader> documentOutHeaders = new HashSet<>();
}
