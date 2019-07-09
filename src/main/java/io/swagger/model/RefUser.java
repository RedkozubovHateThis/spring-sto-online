package io.swagger.model;

import java.util.Date;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.threeten.bp.OffsetDateTime;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * RefUser
 */
@Entity
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
@Table(name = "ref_user")
public class RefUser   {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonProperty("id")
  private Integer id = null;

  @Column(name ="first_name")
  @JsonProperty("first_name")
  private String firstName = null;

  @Column(name ="middle_name")
  @JsonProperty("middle_name")
  private String middleName = null;

  @Column(name ="last_name")
  @JsonProperty("last_name")
  private String lastName = null;

  @Column(name ="username")
  @JsonProperty("username")
  private String username = null;

  @Column(name ="password")
  @JsonProperty("password")
  private String password = null;

  @Column(name ="email")
  @JsonProperty("email")
  private String email = null;

  @Column(name ="created_at")
  @JsonProperty("created_at")
  private Date createdAt = null;

  @Column(name ="updated_at")
  @JsonProperty("updated_at")
  private Date updatedAt = null;

  public RefUser id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * идентификатор пользователя
   * @return id
  **/
  @ApiModelProperty(value = "идентификатор пользователя")

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public RefUser firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
  **/
  @ApiModelProperty(value = "")

@Size(max=64)   public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public RefUser middleName(String middleName) {
    this.middleName = middleName;
    return this;
  }

  /**
   * Get middleName
   * @return middleName
  **/
  @ApiModelProperty(value = "")

@Size(max=64)   public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public RefUser lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
  **/
  @ApiModelProperty(value = "")

@Size(max=64)   public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public RefUser username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Get username
   * @return username
  **/
  @ApiModelProperty(value = "")

@Size(max=32)   public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public RefUser password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
  **/
  @ApiModelProperty(value = "")

@Size(max=255)   public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public RefUser email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
  **/
  @ApiModelProperty(value = "")

@Size(max=255)   public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public RefUser createdAt(Date createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  **/
  @ApiModelProperty(value = "")

  @Valid
  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public RefUser updatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
  **/
  @ApiModelProperty(value = "")

  @Valid
  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefUser refUser = (RefUser) o;
    return Objects.equals(this.id, refUser.id) &&
        Objects.equals(this.firstName, refUser.firstName) &&
        Objects.equals(this.middleName, refUser.middleName) &&
        Objects.equals(this.lastName, refUser.lastName) &&
        Objects.equals(this.username, refUser.username) &&
        Objects.equals(this.password, refUser.password) &&
        Objects.equals(this.email, refUser.email) &&
        Objects.equals(this.createdAt, refUser.createdAt) &&
        Objects.equals(this.updatedAt, refUser.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, middleName, lastName, username, password, email, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefUser {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
