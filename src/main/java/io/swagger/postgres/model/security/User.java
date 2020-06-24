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
@EqualsAndHashCode(callSuper = true)
@Where(clause = "enabled=true")
@JsonApiResource(type = "user", resourcePath = "users")
public class User extends BaseEntity implements UserDetails, Serializable {

    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String email;
    private String inn;
    private String vin;
    private Boolean isAutoRegistered;

    @Type(type ="io.swagger.config.database.StringArrayUserType")
    private String[] vinNumbers;

    @JsonIgnore
    private boolean accountExpired;
    @JsonIgnore
    private boolean accountLocked;
    @JsonIgnore
    private boolean credentialsExpired;

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

    @Transient
    private Long currentSubscriptionId;

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

    private Long subscriptionTypeId;

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    public String getFio() {
        if ( this.middleName == null && this.firstName == null )
            return this.lastName;
        else if ( this.middleName == null && this.firstName != null )
            return this.lastName + " " + this.firstName.substring(0, 1) + ".";
        else if ( this.middleName != null && this.firstName == null )
            return this.lastName + " " + this.middleName.substring(0, 1) + ".";
        else if ( this.middleName != null && this.firstName != null )
            return this.lastName + " " + this.firstName.substring(0, 1) + ". " +
                    this.middleName.substring(0, 1) + ".";
        else
            return null;
    }

    public Boolean isUserAdmin() {
        return UserHelper.isAdmin( this );
    }

    public Boolean isUserClient() {
        return UserHelper.isClient( this );
    }

    public Boolean isUserServiceLeader() {
        return UserHelper.isServiceLeader( this );
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCurrentSubscriptionId() {
        if ( currentSubscription != null )
            return currentSubscription.getId();

        return null;
    }

    @JsonIgnore
    public Long getCurrentCurrentSubscriptionId() {
        return currentSubscriptionId;
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
