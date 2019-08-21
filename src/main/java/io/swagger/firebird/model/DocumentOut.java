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
@Table(name = "DOCUMENT_OUT")
@Data
@EqualsAndHashCode(of = "id")
public class DocumentOut {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_DOCUMENT_OUT")
            })
    @Column(name = "DOCUMENT_OUT_ID")
    private Integer id;

    @Column(name = "DOCUMENT_TYPE_ID")
    private Integer documentType;
    @Column(name = "DOCUMENT_IN_ID")
    private Integer documentIn;
    @Column(name = "SOURCE_DOCUMENT_OUT_ID")
    private Integer sourceDocumentOut;

    @JoinColumn(name = "ORGANIZATION_ID")
    @ManyToOne
    private Organization organization;

    @Column(name = "ORGANIZATION_CONTACT_ID")
    private Integer organizationContact;
    @Column(name = "ORGANIZATION_REQUISITE_ID")
    private Integer organizationRequisite;
    @JoinColumn(name = "CLIENT_ID")
    @ManyToOne
    private Client client;
    @Column(name = "CLIENT_CONTACT_ID")
    private Integer clientContact;
    @Column(name = "CLIENT_REQUISITE_ID")
    private Integer clientRequisite;
    @Column(name = "SOURCE_INFO_ID")
    private Integer sourceInfo;
    @Column(name = "DISCONT_CARD_ID")
    private Integer discontCard;

    @Column(name = "DIVISION_COUNT")
    private Integer divisionCount;
    @Column(name = "RETURN_COUNT")
    private Integer returnCount;
    @Column(name = "ROUND_VALUE")
    private Integer roundValue;
    @Column(name = "SUMMA", columnDefinition = "NUMERIC")
    private Double summa;
    @Column(name = "SUMMA_BONUS", columnDefinition = "NUMERIC")
    private Double summaBonus;

    @Column(name = "DATE_ACCEPT")
    private Date dateAccept;
    @Column(name = "DATE_PAYMENT")
    private Date datePayment;

    @Column(name = "DISCOUNT")
    private Double discount;

    @Column(name = "FLAG")
    private Short flag;

    @Column(name = "BASIS_DOC")
    private String basisDoc;
    @Column(name = "BASIS_NUMBER")
    private String basisNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "BASIS_DATE")
    private Date basisDate;

    @JsonIgnore
    @OneToMany(mappedBy = "documentOut")
    private Set<DocumentOutHeader> documentOutHeaders = new HashSet<>();

    @OneToMany(mappedBy = "documentOut", fetch = FetchType.EAGER)
    private Set<ServiceWork> serviceWorks = new HashSet<>();

    @OneToMany(mappedBy = "documentOut", fetch = FetchType.EAGER)
    @OrderBy("positionNumber")
    private Set<GoodsOutClient> goodsOutClients = new HashSet<>();

    @OneToMany(mappedBy = "documentOut", fetch = FetchType.EAGER)
    @OrderBy("positionNumber")
    private Set<GoodsOut> goodsOuts = new HashSet<>();

}
