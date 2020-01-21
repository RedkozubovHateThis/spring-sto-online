package io.swagger.postgres.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.helper.UserHelper;
import io.swagger.postgres.model.ChatMessage;
import io.swagger.postgres.model.DocumentUserState;
import io.swagger.postgres.model.EventMessage;
import io.swagger.postgres.model.UploadFile;
import io.swagger.postgres.model.payment.Subscription;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
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
@EqualsAndHashCode(of = "id")
@Where(clause = "enabled=true")
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private Integer clientId;
    private Integer organizationId;
    private Boolean isApproved;
    private Date lastUserAcceptDate;
    private Boolean inVacation;
    private Boolean isAutoRegistered;
//    private Long replacementModeratorId;

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
    private Set<UserRole> roles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "fromUser")
    @OrderBy("messageDate")
    private Set<ChatMessage> messagesAsFrom = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "toUser")
    @OrderBy("messageDate")
    private Set<ChatMessage> messagesAsTo = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "uploadUser")
    @OrderBy("uploadDate")
    private Set<UploadFile> uploadFiles = new HashSet<>();

    @JsonIgnore
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User replacementModerator;
    @JsonIgnore
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private User moderator;

    @JsonIgnore
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private Subscription currentSubscription;
    @JsonIgnore
    @OrderBy("startDate desc")
    @OneToMany(mappedBy = "user")
    private Set<Subscription> allSubscriptions = new HashSet<>();

    @Transient
    private Long replacementModeratorId;
    @Transient
    private Long moderatorId;
    @Transient
    private Long currentSubscriptionId;

    @JsonIgnore
    @OneToMany(mappedBy = "replacementModerator")
    @OrderBy("lastName")
    @Where(clause = "enabled=true")
    private Set<User> replacedBy = new HashSet<>();

    @JsonIgnore
    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private DocumentUserState documentUserState;

    @JsonIgnore
    @OneToMany(mappedBy = "sendUser")
    @OrderBy("messageDate desc")
    private Set<EventMessage> messagesAsUser = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "targetUser")
    @OrderBy("messageDate desc")
    private Set<EventMessage> messagesAsTargetUser = new HashSet<>();

    @Type(type ="io.swagger.config.database.GenericArrayUserType")
    private Integer[] partShops;

    private Boolean allowSms;

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
        return UserHelper.hasRole( this, "ADMIN" );
    }

    public Boolean isUserClient() {
        return UserHelper.hasRole( this, "CLIENT" );
    }

    public Boolean isUserGuest() {
        return UserHelper.hasRole( this, "GUEST" );
    }

    public Boolean isUserServiceLeader() {
        return UserHelper.hasRole( this, "SERVICE_LEADER" );
    }

    public Boolean isUserModerator() {
        return UserHelper.hasRole( this, "MODERATOR" );
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public String getReplacementModeratorFio() {
        if ( replacementModerator != null )
            return replacementModerator.getFio();

        return null;
    }

    public Long getReplacementModeratorId() {
        if ( replacementModerator != null )
            return replacementModerator.getId();

        return null;
    }

    public String getModeratorFio() {
        if ( moderator != null )
            return moderator.getFio();

        return null;
    }

    public Long getModeratorId() {
        if ( moderator != null )
            return moderator.getId();

        return null;
    }

    public Long getCurrentSubscriptionId() {
        if ( currentSubscription != null )
            return currentSubscription.getId();

        return null;
    }

    @JsonIgnore
    public Long getCurrentReplacementModeratorId() {
        return replacementModeratorId;
    }

    @JsonIgnore
    public Long getCurrentModeratorId() {
        return moderatorId;
    }

    @JsonIgnore
    public Long getCurrentCurrentSubscriptionId() {
        return currentSubscriptionId;
    }

    public Double getBalance() {
        if ( balance == null ) return 0.0;
        return balance;
    }

    public Boolean getIsCurrentSubscriptionEmpty() {
        return currentSubscription == null;
    }
    public Boolean getIsBalanceInvalid() {
        return balance != null && balance < 0;
    }
    public Boolean getIsAccessRestricted() {
        return getIsCurrentSubscriptionEmpty() || getIsBalanceInvalid();
    }

    public Boolean getIsCurrentSubscriptionExpired() {
        return currentSubscription != null && currentSubscription.getEndDate().before( new Date() );
    }
}
