package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * RegOrder
 */
@Entity
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
@Table(name = "reg_order")
public class RegOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("number")
  @Column(name = "number")
  private String number;

  @JsonProperty("start_date")
  @Column(name = "start_date")
  private Date startDate;

  @JsonProperty("end_date")
  @Column(name = "end_date")
  private Date endDate;

  @JsonProperty("state")
  @Column(name = "state")
  private Integer state;

  @JsonProperty("client")
  @Column(name = "client")
  private String client;

  @JsonProperty("car")
  @Column(name = "car")
  private String car;

  @JsonProperty("sum")
  @Column(name = "sum")
  private Float sum;

  @JsonProperty("balance")
  @Column(name = "balance")
  private Float balance;

  public RegOrder id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * идентификатор наряда
   * @return id
  */
  @ApiModelProperty(value = "идентификатор наряда")


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public RegOrder number(String number) {
    this.number = number;
    return this;
  }

  /**
   * номер наряда
   * @return number
  */
  @ApiModelProperty(value = "номер наряда")

@Size(max=100)
  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public RegOrder startDate(Date startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
  */
  @ApiModelProperty(value = "")

  @Valid

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public RegOrder endDate(Date endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
  */
  @ApiModelProperty(value = "")

  @Valid

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public RegOrder state(Integer state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   * @return state
  */
  @ApiModelProperty(value = "")


  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public RegOrder client(String client) {
    this.client = client;
    return this;
  }

  /**
   * Get client
   * @return client
  */
  @ApiModelProperty(value = "")

@Size(max=255)
  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public RegOrder car(String car) {
    this.car = car;
    return this;
  }

  /**
   * Get car
   * @return car
  */
  @ApiModelProperty(value = "")

@Size(max=255)
  public String getCar() {
    return car;
  }

  public void setCar(String car) {
    this.car = car;
  }

  public RegOrder sum(Float sum) {
    this.sum = sum;
    return this;
  }

  /**
   * Get sum
   * @return sum
  */
  @ApiModelProperty(value = "")


  public Float getSum() {
    return sum;
  }

  public void setSum(Float sum) {
    this.sum = sum;
  }

  public RegOrder balance(Float balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
   * @return balance
  */
  @ApiModelProperty(value = "")


  public Float getBalance() {
    return balance;
  }

  public void setBalance(Float balance) {
    this.balance = balance;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegOrder regOrder = (RegOrder) o;
    return Objects.equals(this.id, regOrder.id) &&
        Objects.equals(this.number, regOrder.number) &&
        Objects.equals(this.startDate, regOrder.startDate) &&
        Objects.equals(this.endDate, regOrder.endDate) &&
        Objects.equals(this.state, regOrder.state) &&
        Objects.equals(this.client, regOrder.client) &&
        Objects.equals(this.car, regOrder.car) &&
        Objects.equals(this.sum, regOrder.sum) &&
        Objects.equals(this.balance, regOrder.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, number, startDate, endDate, state, client, car, sum, balance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegOrder {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    number: ").append(toIndentedString(number)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    client: ").append(toIndentedString(client)).append("\n");
    sb.append("    car: ").append(toIndentedString(car)).append("\n");
    sb.append("    sum: ").append(toIndentedString(sum)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

