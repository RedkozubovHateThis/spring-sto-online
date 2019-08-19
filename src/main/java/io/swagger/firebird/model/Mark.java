package io.swagger.firebird.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MARK")
@Data
@EqualsAndHashCode(of = "id")
public class Mark {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_MARK")
            })
    @Column(name = "MARK_ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "HIDDEN")
    private Short hidden;

    @JsonIgnore
    @OneToMany(mappedBy = "mark")
    private Set<Model> models = new HashSet<>();

}
