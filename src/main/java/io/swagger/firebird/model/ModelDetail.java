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
@Table(name = "MODEL_DETAIL")
@Data
@EqualsAndHashCode(of = "id")
public class ModelDetail {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_MODEL_DETAIL")
            })
    @Column(name = "MODEL_DETAIL_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "MODEL_ID")
    private Model model;
    @Column(name = "DIRECTORY_REGISTRY_ID")
    private Integer directoryRegistry;
    @ManyToOne
    @JoinColumn(name = "COLOR_ID")
    private Color color;

    @Column(name = "CAR_GEARBOX_TYPE_ID")
    private Integer carGearboxType;
    @Column(name = "CAR_ENGINE_TYPE_ID")
    private Integer carEngineType;
    @ManyToOne
    @JoinColumn(name = "CAR_BODY_TYPE_ID")
    private CarBodyType carBodyType;
    @Column(name = "CAR_FUEL_TYPE_ID")
    private Integer carFuelType;
    @Column(name = "CAR_BRAKE_TYPE_ID")
    private Integer carBreakType;

    @Temporal(TemporalType.DATE)
    @Column(name = "YEAR_OF_PRODUCTION")
    private Date yearOfProduction;

    @Column(name = "POWER_ENGINE", columnDefinition = "NUMERIC")
    private Double powerEngine;
    @Column(name = "POWER_ENGINE_WATT", columnDefinition = "NUMERIC")
    private Double powerEngineWatt;

    @Column(name = "REGNO")
    private String regNumber;
    @Column(name = "NORMALIZED_REGNO")
    private String normalizedRegNumber;
    @Column(name = "VIN")
    private String vinNumber;
    @Column(name = "CHASSIS")
    private String chassisNumber;
    @Column(name = "BODY")
    private String bodyNumber;
    @Column(name = "ENGINE_NUMBER")
    private String engineNumber;

    @Column(name = "PLACE_PASSENGER")
    private Integer placePassenger;

    @Column(name = "MAX_MASS")
    private Double maxMass;
    @Column(name = "TONNAGE")
    private Double tonnage;
    @Column(name = "EMPTY_MASS")
    private Double emptyMass;

    @Lob
    @Column(name = "NOTES")
    private String notes;
    @Lob
    @Column(name = "DOP_INFO")
    private String dopInfo;

    @JsonIgnore
    @OneToMany(mappedBy = "modelDetail")
    private Set<ModelLink> modelLinks = new HashSet<>();

    public String getColorName() {
        if ( color == null ) return null;
        return color.getName();
    }

    public String getCarBodyTypeName() {
        if ( carBodyType == null ) return null;
        return carBodyType.getName();
    }

}
