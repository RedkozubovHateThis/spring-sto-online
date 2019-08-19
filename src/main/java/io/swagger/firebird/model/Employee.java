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
@Table(name = "EMPLOYEE")
@Data
@EqualsAndHashCode(of = "id")
public class Employee {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_EMPLOYEE")
            })
    @Column(name = "EMPLOYEE_ID")
    private Integer id;

    @Column(name = "DIRECTORY_REGISTRY_ID")
    private Integer directoryRegistry;

    @Column(name = "FIRSTNAME")
    private String firstName;
    @Column(name = "MIDDLENAME")
    private String middleName;
    @Column(name = "LASTNAME")
    private String lastName;
    @Column(name = "FULLNAME")
    private String fullName;
    @Column(name = "SHORTNAME")
    private String shortName;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTH")
    private Date birthDate;

    @Column(name = "SEX")
    private Short sex;

    @Lob
    @Column(name = "NOTES")
    private String notes;

    @Column(name = "HIDDEN")
    private Short hidden;

    @JsonIgnore
    @Lob
    @Column(name = "PHOTO")
    private byte[] photoSource;
    @JsonIgnore
    @Lob
    @Column(name = "SIGNATURE")
    private byte[] signatureSource;

    @Column(name = "BAR_CODE")
    private String barCode;
    @Column(name = "INN")
    private String inn;

    @Column(name = "EXTENTION_NUMBER")
    private Integer extentionNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "employee")
    private Set<User> users = new HashSet<>();

}
