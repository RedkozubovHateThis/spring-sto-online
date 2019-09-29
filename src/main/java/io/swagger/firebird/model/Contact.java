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
@Table(name = "CONTACT")
@Data
@EqualsAndHashCode(of = "id")
public class Contact {

    @Id
    @GeneratedValue(generator = "GEN", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "GEN_CONTACT")
            })
    @Column(name = "CONTACT_ID")
    private Integer id;

    @Column(name = "DIRECTORY_REGISTRY_LINK_ID")
    private Integer directoryRegistryLink;

    @Column(name = "ZIPCODE")
    private String zipCode;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "DISTRICT")
    private String district;
    @Column(name = "TOWN")
    private String town;
    @Column(name = "STREET")
    private String street;
    @Column(name = "HOUSE")
    private String house;
    @Column(name = "BUILDING")
    private String building;
    @Column(name = "FLAT")
    private String flat;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "FAX")
    private String fax;
    @Column(name = "WWW")
    private String www;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "ICQ")
    private String icq;
    @Column(name = "MOBILE")
    private String mobile;
    @Column(name = "CONTACT_FULL")
    private String contactFull;

    @Column(name = "FACE")
    private Short face;
    @Column(name = "HIDDEN")
    private Short hidden;
    @Column(name = "DEFAULT_CONTACT")
    private Short defaultContact;
    @Column(name = "ALLOW_SMS")
    private Short allowSms;
    @Column(name = "ALLOW_EMAIL")
    private Short allowEmail;

    @Lob
    @Column(name = "NOTES")
    private String notes;

}
