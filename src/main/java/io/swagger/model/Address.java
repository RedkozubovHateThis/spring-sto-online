package io.swagger.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ADDRESS")
@Getter
@Setter
@ApiModel
public class Address implements Serializable {

    @ApiModelProperty(value = "The database generated address id", required = true, readOnly = true)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id = null;

    @ApiModelProperty(value = "Street name", example = "Street YY")
    @Column(name = "STREET")
    private String street;

    @ApiModelProperty(value = "House number", example = "6")
    @Column(name = "HOUSE_NUMBER")
    private String houseNumber;

    @ApiModelProperty(value = "Address ZIP code", example = "12-345")
    @Column(name = "ZIP_CODE")
    private String zipCode;
}
