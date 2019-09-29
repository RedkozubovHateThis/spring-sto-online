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
@Table(name = "EXECUTOR")
@Data
public class Executor {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_BRIGADE_STRUCTURE")
            })
    @Column(name = "BRIGADE_STRUCTURE_ID")
    private Integer id;

    @Column(name = "BRIGADE_STRUCTURE_ID", insertable = false, updatable = false)
    private Integer brigadeStructure;
    @Column(name = "BRIGADE_EXECUTOR_ID")
    private Integer brigadeExecutor;
    @OneToOne
    @JoinColumn(name = "SERVICE_WORK_ID")
    private ServiceWork serviceWork;
    @Column(name = "DIRECTORY_REGISTRY_ID")
    private Integer directoryRegistry;
    @Column(name = "EXECUTOR_REGISTRY_ID")
    private Integer executorRegistry;
    @Column(name = "EMPLOYEE_ID")
    private Integer employee;
    @Column(name = "PROVIDER_ID")
    private Integer provider;
    @Column(name = "CLIENT_ID")
    private Integer client;

    @Column(name = "FULLNAME")
    private String fullName;
    @Column(name = "SHORTNAME")
    private String shortName;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTH")
    private Date birthDate;

    @Column(name = "SEX")
    private Short sex;
    @Column(name = "HIDDEN")
    private Short hidden;

    @Column(name = "TARIFF")
    private Double tariff;
    @Column(name = "PERCENT_EXEC_WORK")
    private Double percentExecWork;
    @Column(name = "PERCENT_WORK_PARTY")
    private Double percentWorkParty;

}
