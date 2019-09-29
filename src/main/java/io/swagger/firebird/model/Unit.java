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
@Table(name = "UNIT")
@Data
@EqualsAndHashCode(of = "id")
public class Unit {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_UNIT")
            })
    @Column(name = "UNIT_ID")
    private Integer id;

    @Column(name = "CODE_OKEY")
    private Integer codeOkey;

    @Column(name = "FULLNAME")
    private String fullName;
    @Column(name = "SHORTNAME")
    private String shortName;

    @Column(name = "HIDDEN")
    private Short hidden;
    @Column(name = "INTEGER_VALUE")
    private Short integerValue;

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    private Set<ServiceGoodsAddon> serviceGoodsAddons = new HashSet<>();

}
