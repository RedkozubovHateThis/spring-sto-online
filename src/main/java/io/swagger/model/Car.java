package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CAR")
@Getter
@Setter
@ApiModel
public class Car implements Serializable {

    @ApiModelProperty(value = "The database generated car id", required = true, readOnly = true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id = null;

    @ApiModelProperty(value = "Registration number", example = "XYZ11ABC")
    @Column(name = "REGISTRATION_NUMBER")
    private String registrationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Company company;
}
