package io.swagger.firebird.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "COLOR")
@Data
@EqualsAndHashCode(of = "id")
public class Color {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_COLOR")
            })
    @Column(name = "COLOR_ID")
    private Integer id;

    @Column(name = "COLOR_VALUE")
    private Integer colorValue;
    @Column(name = "SYSTEM_ID")
    private Integer systemId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "HIDDEN")
    private Short hidden;

}
