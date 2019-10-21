package io.swagger.firebird.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "ACT_DEFECTION_CHECK")
@Data
@EqualsAndHashCode(of = "id")
public class ActDefectionCheck {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_ACT_DEFECTION_CHECK")
            })
    @Column(name = "ACT_DEFECTION_CHECK_ID")
    private Integer id;
    @Column(name = "PARENT_ID")
    private Integer parent;

    @Column(name = "NAME")
    private String name;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "NODE_POSITION")
    private Integer nodePosition;

    @Column(name = "CHECK_ALL")
    private Short checkAll;

    public Map<String, Object> buildReportData(boolean isFirst) {
        Map<String, Object> reportData = new HashMap<>();

        reportData.put("name", name);
        if ( isFirst ) {
            reportData.put("detectedLeft", "Левый");
            reportData.put("detectedRight", "Правый");
        }
        reportData.put("detected", null);
        reportData.put("description", null);
        reportData.put("fixed", null);
        reportData.put("hasParent", parent != null);

        return reportData;
    }

}
