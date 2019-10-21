package io.swagger.firebird.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "INSPECTION_CAR_IMAGE")
@Data
@EqualsAndHashCode(of = "id")
public class InspectionCarImage {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_INSPECTION_CAR_IMAGE")
            })
    @Column(name = "INSPECTION_CAR_IMAGE_ID")
    private Integer id;

    @JsonIgnore
    @Lob
    @Column(name = "PICTURE")
    private byte[] pictureSource;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SYSTEM_ID")
    private Integer systemId;

    @Column(name = "HIDDEN")
    private Short hidden;
    @Column(name = "CLOUD_UPLOAD_VERSION")
    private Short cloudUploadVersion;

    @Column(name = "CLOUD_UPLOAD_TIME")
    private Date cloudUploadTime;
    @Column(name = "LAST_CHANGE")
    private Date lastChange;

}
