package io.swagger.firebird.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "MANAGER")
@Data
@EqualsAndHashCode(of = "id")
public class Manager {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_MANAGER")
            })
    @Column(name = "MANAGER_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ORGANIZATION_STRUCTURE_ID")
    private OrganizationStructure organizationStructure;

    @Column(name = "HIDDEN")
    private Short hidden;

    @Column(name = "PERCENT_EXEC")
    private Double percentExec;
    @Column(name = "TARIFF")
    private Double tariff;
    @Column(name = "PERCENT_GOODS")
    private Double percentGoods;

}
