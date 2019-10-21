package io.swagger.firebird.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "CAR_ENGINE_TYPE")
@Data
@EqualsAndHashCode(of = "id")
public class CarEngineType {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_CAR_ENGINE_TYPE")
            })
    @Column(name = "CAR_ENGINE_TYPE_ID")
    private Integer id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SYSTEM_ID")
    private Short systemId;
    @Column(name = "HIDDEN")
    private Short hidden;

}
