package io.swagger.firebird.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "SERVICE_GOODS_ADDON")
@Data
@EqualsAndHashCode(of = "id")
public class ServiceGoodsAddon {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_SERVICE_GOODS_ADDON")
            })
    @Column(name = "SERVICE_GOODS_ADDON_ID")
    private Integer id;

    @Column(name = "UNIT_ID")
    private Integer unit;
    @Column(name = "AC_DOC_ID")
    private Integer acDoc;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_OUT_ID")
    private DocumentOut documentOut;

    @Column(name = "COST", columnDefinition = "NUMERIC")
    private Double cost;
    @Column(name = "DISCOUNT")
    private Double discount;
    @Column(name = "DISCOUNT_FIX", columnDefinition = "NUMERIC")
    private Double discountFix;
    @Column(name = "CORRECTION_SUMMA", columnDefinition = "NUMERIC")
    private Double correctionSumma;

    @Column(name = "GOODS_COUNT", columnDefinition = "NUMERIC")
    private Integer goodsCount;
    @Column(name = "POSITION_NUMBER")
    private Integer positionNumber;
    @Column(name = "UID")
    private Integer uid;

    @Lob
    @Column(name = "NOTES")
    private String notes;

    @Column(name = "FULLNAME")
    private String fullName;
    @Column(name = "NUMBER")
    private String number;
    @Column(name = "AUDATEX_UID")
    private String audatexUid;

}
