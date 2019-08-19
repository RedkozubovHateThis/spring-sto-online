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
@Table(name = "CLIENT")
@Data
@EqualsAndHashCode(of = "id")
public class Client {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_CLIENT")
            })
    @Column(name = "CLIENT_ID")
    private Integer id;

    @Column(name = "DIRECTORY_REGISTRY_ID")
    private Integer directoryRegistry;
    @Column(name = "CLIENT_TREE_ID")
    private Integer clientTree;
    @Column(name = "PRICE_NORM_ID")
    private Integer priceNorm;

    @Column(name = "FULLNAME")
    private String fullName;
    @Column(name = "SHORTNAME")
    private String shortName;
    @Column(name = "INN")
    private String inn;
    @Column(name = "KPP")
    private String kpp;
    @Column(name = "SALUTATION")
    private String salutation;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTH")
    private Date birthDate;

    @Column(name = "SEX")
    private Short sex;
    @Column(name = "FACE")
    private Short face;
    @Column(name = "MARK")
    private Short mark;
    @Column(name = "HIDDEN")
    private Short hidden;
    @Column(name = "REMINDER_ACTIVE")
    private Short reminderActive;
    @Column(name = "REMINDER_SWITCH")
    private Short reminderSwitch;
    @Column(name = "QUESTIONARY_ACTIVE")
    private Short questionaryActive;

    @Lob
    @Column(name = "NOTES")
    private String notes;

    @Column(name = "DATE_PAYMENT")
    private Integer datePayment;
    @Column(name = "PRICE_COST_NUM")
    private Integer priceCostNum;

    @Column(name = "DISCOUNT")
    private Double discount;
    @Column(name = "DISCOUNT_WORK")
    private Double discountWork;

    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private Set<DocumentOut> documentOuts = new HashSet<>();

}
