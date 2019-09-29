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
@Table(name = "DOCUMENT_OUT_HEADER")
@Data
@EqualsAndHashCode(of = "id")
public class DocumentOutHeader {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_DOCUMENT_OUT_HEADER")
            })
    @Column(name = "DOCUMENT_OUT_HEADER_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "DOCUMENT_OUT_ID")
    private DocumentOut documentOut;

    @Column(name = "DOCUMENT_OUT_TREE_ID")
    private Integer documentOutTree;
    @Column(name = "DOCUMENT_REGISTRY_ID")
    private Integer documentRegistry;
    @Column(name = "DOCUMENT_TYPE_ID")
    private Integer documentType;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "PREFIX")
    private String prefix;
    @Column(name = "SUFFIX")
    private String suffix;

    @Column(name = "NUMBER")
    private Integer number;
    @Column(name = "MARK")
    private Integer mark;
    @Column(name = "STATE")
    private Integer state;

    @Column(name = "DATE_CREATE")
    private Date dateCreate;

    @Lob
    @Column(name = "NOTES")
    private String notes;
    @Lob
    @Column(name = "INFO")
    private String info;
    @Lob
    @Column(name = "ABCP_ORDER")
    private String abcpOrder;

    @Column(name = "FULLNUMBER")
    private String fullNumber;
    @Column(name = "BARCODE")
    private String barcode;

    @ManyToOne
    @JoinColumn(name = "MANAGER_ID")
    private Manager manager;
    @Column(name = "CONTRACT_ID")
    private Integer contract;

    @JsonIgnore
    @OneToMany(mappedBy = "documentOutHeader")
    private Set<DocumentServiceDetail> documentServiceDetails = new HashSet<>();
}
