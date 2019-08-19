package io.swagger.firebird.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "MODEL_LINK")
@Data
@EqualsAndHashCode(of = "id")
public class ModelLink {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_MODEL_LINK")
            })
    @Column(name = "MODEL_LINK_ID")
    private Integer id;

    @Column(name = "DIRECTORY_REGISTRY_LINK_ID")
    private Integer directoryRegistryLink;
    @JoinColumn(name = "MODEL_DETAIL_ID")
    @ManyToOne
    private ModelDetail modelDetail;

    @Column(name = "HIDDEN")
    private Short hidden;
    @Column(name = "DEFAULT_CAR")
    private Short defaultCar;

    @Column(name = "RUN_AVERAGE_DAY")
    private Double runAverageDay;

    @JsonIgnore
    @OneToMany(mappedBy = "modelLink")
    private Set<DocumentServiceDetail> documentServiceDetails = new HashSet<>();

}
