package io.swagger.firebird.model;

import io.swagger.response.report.ClientsNativeResponse;
import io.swagger.response.report.ExecutorsNativeResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "byExecutors",
                classes = {
                        @ConstructorResult(
                                targetClass = ExecutorsNativeResponse.class,
                                columns = {
                                        @ColumnResult( name="FULL_NAME", type = String.class ),
                                        @ColumnResult( name="DSD_ID", type = Integer.class ),
                                        @ColumnResult( name="DATE_START", type = Date.class ),
                                        @ColumnResult( name="PERCENT", type = Double.class ),
                                        @ColumnResult( name="TOTAL_BY_PRICE", type = Double.class ),
                                        @ColumnResult( name="TOTAL_BY_NORM", type = Double.class ),
                                }
                        )
                }
        ),
        @SqlResultSetMapping(
                name = "byClients",
                classes = {
                        @ConstructorResult(
                                targetClass = ClientsNativeResponse.class,
                                columns = {
                                        @ColumnResult( name="CLIENT_ID", type = Integer.class ),
                                        @ColumnResult( name="FULL_NAME", type = String.class ),
                                        @ColumnResult( name="DSD_ID", type = Integer.class ),
                                        @ColumnResult( name="DATE_START", type = Date.class ),
                                        @ColumnResult( name="TOTAL", type = Double.class )
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "DocumentServiceDetail.findExecutors", query="SELECT e.SHORTNAME AS FULL_NAME, dsd.DOCUMENT_SERVICE_DETAIL_ID AS DSD_ID, dsd.DATE_START AS DATE_START, e.PERCENT_EXEC_WORK AS PERCENT,\n" +
                "SUM( IIF( sw.PRICE_NORM IS NOT NULL AND sw.TIME_VALUE IS NOT NULL, 0, SW.PRICE * sw.QUANTITY ) ) AS TOTAL_BY_PRICE,\n" +
                "SUM( IIF( sw.PRICE_NORM IS NOT NULL AND sw.TIME_VALUE IS NOT NULL, sw.PRICE_NORM * sw.TIME_VALUE * sw.QUANTITY, 0 ) ) AS TOTAL_BY_NORM\n" +
                "FROM DOCUMENT_SERVICE_DETAIL AS dsd\n" +
                "INNER JOIN DOCUMENT_OUT_HEADER AS doh ON doh.DOCUMENT_OUT_HEADER_ID = dsd.DOCUMENT_OUT_HEADER_ID\n" +
                "INNER JOIN DOCUMENT_OUT AS do ON do.DOCUMENT_OUT_ID = doh.DOCUMENT_OUT_ID\n" +
                "INNER JOIN ORGANIZATION AS o ON o.ORGANIZATION_ID = do.ORGANIZATION_ID\n" +
                "INNER JOIN SERVICE_WORK AS sw ON sw.DOCUMENT_OUT_ID = do.DOCUMENT_OUT_ID\n" +
                "INNER JOIN EXECUTOR AS e ON e.SERVICE_WORK_ID = sw.SERVICE_WORK_ID\n" +
                "WHERE o.ORGANIZATION_ID = :organizationId AND dsd.DATE_START BETWEEN :startDate AND :endDate\n" +
                "GROUP BY e.SHORTNAME, dsd.DOCUMENT_SERVICE_DETAIL_ID, dsd.DATE_START, e.PERCENT_EXEC_WORK\n" +
                "ORDER BY e.SHORTNAME, dsd.DOCUMENT_SERVICE_DETAIL_ID, dsd.DATE_START", resultSetMapping = "byExecutors"),
        @NamedNativeQuery(name = "DocumentServiceDetail.findClients", query="SELECT c.CLIENT_ID AS CLIENT_ID, c.SHORTNAME AS FULL_NAME, dsd.DOCUMENT_SERVICE_DETAIL_ID AS DSD_ID, dsd.DATE_START AS DATE_START, \n" +
                "SUM( IIF( sw.PRICE_NORM IS NOT NULL AND sw.TIME_VALUE IS NOT NULL, sw.PRICE_NORM * sw.TIME_VALUE, SW.PRICE ) * sw.QUANTITY ) AS TOTAL \n" +
                "FROM DOCUMENT_SERVICE_DETAIL AS dsd\n" +
                "INNER JOIN DOCUMENT_OUT_HEADER AS doh ON doh.DOCUMENT_OUT_HEADER_ID = dsd.DOCUMENT_OUT_HEADER_ID\n" +
                "INNER JOIN DOCUMENT_OUT AS do ON do.DOCUMENT_OUT_ID = doh.DOCUMENT_OUT_ID\n" +
                "INNER JOIN ORGANIZATION AS o ON o.ORGANIZATION_ID = do.ORGANIZATION_ID\n" +
                "INNER JOIN SERVICE_WORK AS sw ON sw.DOCUMENT_OUT_ID = do.DOCUMENT_OUT_ID\n" +
                "INNER JOIN CLIENT AS c ON c.CLIENT_ID = do.CLIENT_ID\n" +
                "INNER JOIN EXECUTOR AS e ON e.SERVICE_WORK_ID = sw.SERVICE_WORK_ID\n" +
                "WHERE o.ORGANIZATION_ID = :organizationId AND dsd.DATE_START BETWEEN :startDate AND :endDate\n" +
                "GROUP BY c.CLIENT_ID, c.SHORTNAME, dsd.DOCUMENT_SERVICE_DETAIL_ID, dsd.DATE_START\n" +
                "ORDER BY c.SHORTNAME, dsd.DOCUMENT_SERVICE_DETAIL_ID, dsd.DATE_START", resultSetMapping = "byClients")
})

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
