package io.swagger.postgres.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.SerializeType;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.BaseEntity;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.payment.Subscription;
import io.swagger.postgres.model.payment.SubscriptionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(of = "id", callSuper = true)
@Where(clause = "enabled=true")
@JsonApiResource(type = "user", resourcePath = "users")
public class User extends BaseEntity implements UserDetails, Serializable {

    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    @Transient
    private String rawPassword;
    private String firstName;
    private String lastName;
    private String middleName;
    private Boolean isAutoRegistered;

    private boolean enabled;

    private Double balance;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_role_id", referencedColumnName = "id"))
    @OrderBy("name")
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Set<UserRole> roles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "uploadUser")
    @OrderBy("uploadDate")
    private Set<UploadFile> uploadFiles = new HashSet<>();

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Subscription currentSubscription;

    @OrderBy("startDate desc")
    @OneToMany(mappedBy = "user")
    @JsonApiRelation(mappedBy = "user")
    private Set<Subscription> allSubscriptions = new HashSet<>();

    @JsonIgnore
    @Transient
    private Collection<SimpleGrantedAuthority> authorities;

    @JsonIgnore
    @OneToMany(mappedBy = "sendUser")
    @OrderBy("messageDate desc")
    private Set<EventMessage> messagesAsUser = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "targetUser")
    @OrderBy("messageDate desc")
    private Set<EventMessage> messagesAsTargetUser = new HashSet<>();

    private Double serviceWorkPrice;
    private Double serviceGoodsCost;

    private String bankBic;
    private String bankName;
    private String checkingAccount;
    private String corrAccount;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private SubscriptionType subscriptionType;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonApiRelation(serialize = SerializeType.EAGER)
    private Profile profile;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getFio() {
        if ( !notEmpty(this.firstName) && !notEmpty(this.middleName) && !notEmpty(this.lastName) )
            return null;

        if ( !notEmpty(this.middleName) && !notEmpty(this.firstName) )
            return this.lastName;
        else if ( !notEmpty(this.middleName) && notEmpty(this.firstName) )
            return this.lastName + " " + this.firstName.substring(0, 1) + ".";
        else if ( notEmpty(this.middleName) && !notEmpty(this.firstName) )
            return this.lastName + " " + this.middleName.substring(0, 1) + ".";
        else if ( notEmpty(this.middleName) && notEmpty(this.firstName) )
            return this.lastName + " " + this.firstName.substring(0, 1) + ". " +
                    this.middleName.substring(0, 1) + ".";
        else
            return null;
    }

    public String getFullFio() {
        if ( !notEmpty(this.firstName) && !notEmpty(this.middleName) && !notEmpty(this.lastName) )
            return null;

        if ( !notEmpty(this.middleName) && !notEmpty(this.firstName) )
            return this.lastName;
        else if ( !notEmpty(this.middleName) && notEmpty(this.firstName) )
            return this.lastName + " " + this.firstName;
        else if ( notEmpty(this.middleName) && !notEmpty(this.firstName) )
            return this.lastName + " " + this.middleName;
        else if ( notEmpty(this.middleName) && notEmpty(this.firstName) )
            return this.lastName + " " + this.firstName + " " +
                    this.middleName;
        else
            return null;
    }

    public String getShortFio() {
        if ( !notEmpty(this.firstName) && !notEmpty(this.middleName) && !notEmpty(this.lastName) )
            return null;

        if ( !notEmpty(this.middleName) && !notEmpty(this.firstName) )
            return this.lastName.substring(0, 1).toUpperCase();
        else if ( !notEmpty(this.middleName) && notEmpty(this.firstName) )
            return (this.lastName.substring(0, 1) + this.firstName.substring(0, 1)).toUpperCase();
        else if ( notEmpty(this.middleName) && !notEmpty(this.firstName) )
            return (this.lastName.substring(0, 1) + this.middleName.substring(0, 1)).toUpperCase();
        else if ( notEmpty(this.middleName) && notEmpty(this.firstName) )
            return (this.lastName.substring(0, 1) + this.firstName.substring(0, 1) +
                    this.middleName.substring(0, 1)).toUpperCase();
        else
            return null;
    }

    private boolean notEmpty(String field) {
        return field != null && field.length() > 0;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        if ( balance == null ) return 0.0;
        return balance;
    }

    public Boolean isCurrentSubscriptionEmpty() {
        return currentSubscription == null;
    }
    public Boolean isBalanceInvalid() {
        return balance != null && balance < 0;
    }

    public Boolean isCurrentSubscriptionExpired() {
        return currentSubscription != null && currentSubscription.getEndDate().before( new Date() );
    }
}
