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
@Table(name = "ORGANIZATION")
@Data
@EqualsAndHashCode(of = "id")
public class Organization {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_ORGANIZATION")
            })
    @Column(name = "ORGANIZATION_ID")
    private Integer id;

    @Column(name = "DIRECTORY_REGISTRY_ID")
    private Integer directoryRegistry;
    @Column(name = "PARENT_ID")
    private Integer parent;
    @Column(name = "TAX_SCHEMES_ID")
    private Integer texSchemes;
    @Column(name = "TAX_SCHEMES_SERVICE_GOODS_ID")
    private Integer texSchemesServiceGoods;
    @Column(name = "TAX_SCHEMES_PREPAYMENT_ID")
    private Integer texSchemesPrepayment;
    @Column(name = "SALE_CLIENT_ID")
    private Integer saleClient;
    @Column(name = "SALE_PROVIDER_ID")
    private Integer saleProvider;

    @Column(name = "FULLNAME")
    private String fullName;
    @Column(name = "SHORTNAME")
    private String shortName;
    @Column(name = "INN")
    private String inn;
    @Column(name = "KPP")
    private String kpp;
    @Column(name = "OKPO")
    private String okpo;
    @Column(name = "RST_TEXT")
    private String rstText;
    @Column(name = "OGRN")
    private String ogrn;
    @Column(name = "DIAGNOSTIC_OPERATOR_NUM")
    private String diagnosticOperatorNum;
    @Column(name = "DIAGNOSTIC_OPERATOR_ATTESTATE")
    private String diagnosticOperatorAttestate;
    @Column(name = "DIAGNOSTIC_OPERATOR_PTO_NUM")
    private String diagnosticOperatorPtoNum;
    @Column(name = "INDIVIDUAL_NAME")
    private String individualName;
    @Column(name = "INDIVIDUAL_REQUISITE")
    private String individualRequisite;
    @Column(name = "COMMERCIALNAME")
    private String commercialName;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "CONTACT_INFO")
    private String contactInfo;
    @Column(name = "FIELD_ACTIVITY")
    private String fieldActivity;

    @Column(name = "DATE_CLOSING_PERIOD")
    private Date dateClosingPeriod;

    @Column(name = "FACE")
    private Short face;
    @Column(name = "HIDDEN")
    private Short hidden;
    @Column(name = "NDS")
    private Short nds;
    @Column(name = "ORDER_OUT")
    private Short orderOut;
    @Column(name = "RST_SHOW")
    private Short rstShow;
    @Column(name = "CAN_SALE")
    private Short canSale;
    @Column(name = "CAN_BUY")
    private Short canBuy;
    @Column(name = "SHOW_DOCUMENT_IN_CLOSING_PERIOD")
    private Short showDocumentInClosingPeriod;
    @Column(name = "PRINT_CHECK")
    private Short printCheck;

    @Lob
    @Column(name = "NOTES")
    private String notes;
    @Lob
    @Column(name = "SERVICE_GUARANTEE")
    private String serviceGuarantee;
    @Lob
    @Column(name = "DESCRIPTION")
    private String description;
    @JsonIgnore
    @Lob
    @Column(name = "LOGO")
    private byte[] logoSource;
    @JsonIgnore
    @Lob
    @Column(name = "STAMP")
    private byte[] stampSource;

    @Column(name = "DATE_PAYMENT")
    private Integer datePayment;

    @Column(name = "COORD_LATITUDE")
    private Double coordLatitude;
    @Column(name = "COORD_LONGITUDE")
    private Double coordLongitude;

    @JsonIgnore
    @OneToMany(mappedBy = "organization")
    private Set<DocumentOut> documentOuts = new HashSet<>();

}
