package io.swagger.firebird.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DOCUMENT_SERVICE_DETAIL")
@Data
@EqualsAndHashCode(of = "id")
public class DocumentServiceDetail {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_DOCUMENT_SERVICE_DETAIL")
            })
    @Column(name = "DOCUMENT_SERVICE_DETAIL_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "DOCUMENT_OUT_HEADER_ID")
    private DocumentOutHeader documentOutHeader;

    @JoinColumn(name = "MODEL_LINK_ID")
    @ManyToOne
    private ModelLink modelLink;

    @Column(name = "REPAIR_TYPE_ID")
    private Integer repairType;

    @Lob
    @Column(name = "SPECIAL_NOTES")
    private String specialNotes;

    @Column(name = "RUN_BEFORE")
    private Integer runBefore;
    @Column(name = "RUN_DURING")
    private Integer runDuring;

    @Column(name = "DISCOUNT_WORK")
    private Double discountWork;
    @Column(name = "SUMMA_WORK", columnDefinition = "NUMERIC")
    private Double summaWork;

    @Lob
    @Column(name = "REASONS_APPEAL")
    private String reasonsAppeal;

    @Column(name = "INDEX_FUEL")
    private Integer indexFuel;

    @Lob
    @Column(name = "EXTERNAL_TEST")
    private String externalTest;
    @Lob
    @Column(name = "STRUCTURE_CAR")
    private String structureCar;

    @Column(name = "LKP")
    private Short lkp;

    @Column(name = "PRICE_NORM_ID")
    private Integer priceNorm;

    @Column(name = "DATE_START")
    private Date dateStart;

    @Column(name = "RT_MODEL_ID")
    private Integer rtModel;
    @Column(name = "RT_DOC_ID")
    private Integer rtDoc;

    @Column(name = "AUTO_UPDATE_DATE")
    private Short autoUpdateDate;

    @Column(name = "INSPECTION_CAR_IMAGE_ID")
    private Integer documentCarImage;
    @Column(name = "PLANNING_WORK_PLACE_ID")
    private Integer planningWorkPlace;

    @Lob
    @Column(name = "ADDON_TERMS")
    private String addonTerms;

    @Column(name = "CAR_COST", columnDefinition = "NUMERIC")
    private Double carCost;

    @Lob
    @Column(name = "GUARANTE")
    private String guarante;

    @Column(name = "SUMMA_BONUS", columnDefinition = "NUMERIC")
    private Double summaBonus;

    @Column(name = "AC_MODEL_ID")
    private Integer acModel;
    @Column(name = "AC_MAP")
    private Integer acMap;

    @Column(name = "DOC_DATE_END_SECTION_LINK")
    private Short docDateEndSectionLink;

    @Column(name = "INSPECTION_COMPLETENESS")
    private String inspectionCompleteness;
}
