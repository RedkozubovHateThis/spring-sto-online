package io.swagger.postgres.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.helper.UserHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(of = "id")
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
    private Integer clientId;
    private Integer organizationId;
    private Long moderatorId;
    private Boolean isApproved;

    @JsonIgnore
    private boolean accountExpired;
    @JsonIgnore
    private boolean accountLocked;
    @JsonIgnore
    private boolean credentialsExpired;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_role_id", referencedColumnName = "id"))
    @OrderBy("name")
    private Set<UserRole> roles = new HashSet<>();

    @JsonIgnore
    @Transient
    private Collection<? extends GrantedAuthority> authorities;

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

    public Boolean isAdmin() {
        return UserHelper.hasRole( this, "ADMIN" );
    }

    public Boolean isClient() {
        return UserHelper.hasRole( this, "CLIENT" );
    }

    public Boolean isGuest() {
        return UserHelper.hasRole( this, "GUEST" );
    }

    public Boolean isServiceLeader() {
        return UserHelper.hasRole( this, "SERVICE_LEADER" );
    }

    public Boolean isModerator() {
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
}
