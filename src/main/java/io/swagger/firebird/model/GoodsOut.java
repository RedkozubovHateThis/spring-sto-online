package io.swagger.firebird.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "GOODS_OUT")
@Data
@EqualsAndHashCode(of = "id")
public class GoodsOut {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_GOODS_OUT")
            })
    @Column(name = "GOODS_OUT_ID")
    private Integer id;

    @Column(name = "GOODS_IN_ID")
    private Integer goodsIn;
    @Column(name = "SHOP_NOMENCLATURE_ID")
    private Integer shopNomenclature;
    @Column(name = "ACTION_GOODS_ID")
    private Integer actionGoods;
    @Column(name = "ACTION_PROMOTION_ID")
    private Integer actionPromotion;
    @Column(name = "REQUEST_EMPLOYEE_ID")
    private Integer requestEmployee;
    @Column(name = "CONFIRMATION_EMPLOYEE_ID")
    private Integer confirmationEmployee;

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
    @Column(name = "GOODS_COUNT_FACT", columnDefinition = "NUMERIC")
    private Integer goodsCountFact;
    @Column(name = "GOODS_COUNT_RETURN", columnDefinition = "NUMERIC")
    private Integer goodsCountReturn;

    @Column(name = "GUARANTE")
    private Short guarante;
    @Column(name = "DISCOUNT_MANUAL")
    private Short discountManual;
    @Column(name = "TRANSFER_STATE")
    private Short transferState;

    @Column(name = "POSITION_NUMBER")
    private Integer positionNumber;

}
