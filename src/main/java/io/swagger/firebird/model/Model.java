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
@Table(name = "MODEL")
@Data
@EqualsAndHashCode(of = "id")
public class Model {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_MODEL")
            })
    @Column(name = "MODEL_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "MARK_ID")
    private Mark mark;
    @Column(name = "RT_MODEL_ID")
    private Integer rtModel;
    @Column(name = "RT_DOC_ID")
    private Integer rtDoc;
    @Column(name = "CAR_GEARBOX_TYPE_ID")
    private Integer carGearboxType;
    @Column(name = "CAR_ENGINE_TYPE_ID")
    private Integer carEngineType;
    @Column(name = "CAR_BODY_TYPE_ID")
    private Integer carBodyType;
    @Column(name = "CAR_FUEL_TYPE_ID")
    private Integer carFuelType;
    @Column(name = "CAR_BRAKE_TYPE_ID")
    private Integer carBreakType;
    @Column(name = "PRICE_NORM_ID")
    private Integer priceNorm;

    @Column(name = "NAME")
    private String name;
    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "POWER_ENGINE", columnDefinition = "NUMERIC")
    private Double powerEngine;
    @Column(name = "POWER_ENGINE_WATT", columnDefinition = "NUMERIC")
    private Double powerEngineWatt;

    @Column(name = "PLACE_PASSENGER")
    private Integer placePassenger;

    @Column(name = "MAX_MASS")
    private Double maxMass;
    @Column(name = "TONNAGE")
    private Double tonnage;
    @Column(name = "EMPTY_MASS")
    private Double emptyMass;

    @JsonIgnore
    @OneToMany(mappedBy = "model")
    private Set<ModelDetail> modelDetails = new HashSet<>();

    public String getFullName() {
        if ( mark == null ) return name;
        return String.format( "%s %s", mark.getName(), name );
    }

}
