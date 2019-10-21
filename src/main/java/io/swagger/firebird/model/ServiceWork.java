package io.swagger.firebird.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "SERVICE_WORK")
@Data
@EqualsAndHashCode(of = "id")
public class ServiceWork {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_SERVICE_WORK")
            })
    @Column(name = "SERVICE_WORK_ID")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_OUT_ID")
    private DocumentOut documentOut;

    @Column(name = "RT_WORK_ID")
    private Integer rtWork;
    @Column(name = "SEARCHING_PHRASE_ID")
    private Integer searchingPhrase;
    @Column(name = "SERVICE_WORK_LINK_ID")
    private Integer serviceWorkLink;
    @Column(name = "ACTION_GOODS_ID")
    private Integer actionGoods;
    @Column(name = "ACTION_PROMOTION_ID")
    private Integer actionPromotion;

    @Column(name = "NAME")
    private String name;
    @Column(name = "CODE")
    private String code;
    @Column(name = "NUMBER")
    private String number;

    @Column(name = "DISCOUNT_WORK")
    private Double discountWork;
    @Column(name = "TIME_VALUE", columnDefinition = "NUMERIC")
    private Double timeValue;
    @Column(name = "PRICE", columnDefinition = "NUMERIC")
    private Double price;
    @Column(name = "FACTOR", columnDefinition = "NUMERIC")
    private Double factor;
    @Column(name = "PRICE_NORM", columnDefinition = "NUMERIC")
    private Double priceNorm;
    @Column(name = "DISCOUNT_WORK_FIX", columnDefinition = "NUMERIC")
    private Double discountWorkFix;
    @Column(name = "CORRECTION_SUMMA", columnDefinition = "NUMERIC")
    private Double correctionSumma;

    @Column(name = "QUANTITY")
    private Integer quantity;
    @Column(name = "WORK_SOURCE")
    private Integer workSource;

    @Lob
    @Column(name = "NOTES")
    private String notes;

    @Column(name = "GUARANTE")
    private Short guarente;
    @Column(name = "DISCOUNT_MANUAL")
    private Short discountManual;
    @Column(name = "RETURNED")
    private Short returned;

    @Temporal(TemporalType.DATE)
    @Column(name = "NEXT_DATE_WORK_DONE")
    private Date nextDateWorkDone;

    @OneToOne(mappedBy = "serviceWork")
    private Executor executor;

    public Map<String, Object> buildReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("workCode", code);
        reportData.put("workName", name);
        reportData.put("quantity", quantity);
        reportData.put("coefficient", factor);
        reportData.put("priceNorm", priceNorm);
        reportData.put("timeValue", timeValue);
        reportData.put("price", price);
        reportData.put("discount", discountWork);
        reportData.put("sum", getServiceWorkTotalCost( false ) );
        if ( executor != null )
            reportData.put("executor", executor.getShortName() );

        return reportData;
    }

    public Map<String, Object> buildShortReportData() {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("workCode", code);
        reportData.put("workName", name);
        reportData.put("quantity", quantity);
        reportData.put("timeValue", timeValue);
        if ( executor != null )
            reportData.put("executor", executor.getShortName() );

        return reportData;
    }

    public Map<String, Object> buildTransferReportData(int number) {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("number", number);
        reportData.put("code", code);
        reportData.put("name", name);
        reportData.put("typeCode", "---");

        if ( priceNorm != null && timeValue != null ) {
            reportData.put("cost", priceNorm);
            reportData.put("count", timeValue);
            reportData.put("measureCode", "356");
            reportData.put("measureName", "ч");
        }
        else {
            reportData.put("cost", price);
            reportData.put("count", 1.0);
            reportData.put("measureCode", "---");
            reportData.put("measureName", "---");
        }

        reportData.put("tax", "---");
        reportData.put("nds", "Без НДС");
        reportData.put("excise", "Без акциза");
        reportData.put("totalCost", getServiceWorkTotalCost(true));
        reportData.put("totalCostWithTax", getServiceWorkTotalCost(true));
        reportData.put("partCountryCode", "---");
        reportData.put("partCountryName", "---");
        reportData.put("declarationCode", "---");

        return reportData;
    }

    public Double getServiceWorkTotalCost(Boolean withDiscount) {

        double workSum = 0.0;
        int quantity = this.quantity != null && this.quantity > 0 ?
                this.quantity : 1;

        if ( priceNorm != null && timeValue != null ) {
            workSum += priceNorm * timeValue;
        }
        else if ( price != null ) {
            workSum += price;
        }

        if ( withDiscount && discountWork != null ) {
            workSum -= discountWork;
        }
        if ( withDiscount && discountWorkFix != null ) {
            workSum -= discountWorkFix;
        }

        return workSum * quantity;
    }

    public Boolean isByPrice() {
        return ( priceNorm == null || timeValue == null ) && price != null;
    }

}
