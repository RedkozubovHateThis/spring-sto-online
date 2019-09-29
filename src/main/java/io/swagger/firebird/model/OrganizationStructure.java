package io.swagger.firebird.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ORGANIZATION_STRUCTURE")
@Data
@EqualsAndHashCode(of = "id")
public class OrganizationStructure {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_ORGANIZATION_STRUCTURE")
            })
    @Column(name = "ORGANIZATION_STRUCTURE_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ORGANIZATION_ID")
    private Organization organization;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID")
    private Employee employee;

    @Column(name = "JOB_ID")
    private Integer job;
    @Column(name = "DEPARTMENT_ID")
    private Integer department;

    @Column(name = "RESPONSIBLE_PERSON_TYPE")
    private Short responsiblePersonType;

    @Temporal(TemporalType.DATE)
    @Column(name = "RESPONSIBLE_PERSON_DATE")
    private Date responsiblePersonDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "BOOKKEEPER_DATE")
    private Date bookKeeperDate;

    @Column(name = "DIAGNOSTIC_EXECUTOR_NAME")
    private String diagnosticExecutorName;
    @Column(name = "DIAGNOSTIC_EXECUTOR_REASON")
    private String diagnosticExecutorReason;

}
