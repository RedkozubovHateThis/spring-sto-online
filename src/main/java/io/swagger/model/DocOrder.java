package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.threeten.bp.OffsetDateTime;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DocOrder
 */
//@Entity
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-04T12:34:26.681Z[GMT]")
public class DocOrder   {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("code")
  private String code = null;

  @JsonProperty("start_date")
  private OffsetDateTime startDate = null;

  @JsonProperty("end_date")
  private OffsetDateTime endDate = null;

  @JsonProperty("ref_car_state_id")
  private Integer refCarStateId = null;

  @JsonProperty("ref_car_id")
  private Integer refCarId = null;

  @JsonProperty("sum")
  private Integer sum = null;

  @JsonProperty("created_at")
  private OffsetDateTime createdAt = null;

  @JsonProperty("updated_at")
  private OffsetDateTime updatedAt = null;

  public DocOrder id(Integer id) {
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

  public DocOrder code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  **/
  @ApiModelProperty(value = "")

@Size(max=64)   public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public DocOrder startDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
  **/
  @ApiModelProperty(value = "")

  @Valid
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
  }

  public DocOrder endDate(OffsetDateTime endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
  **/
  @ApiModelProperty(value = "")

  @Valid
  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(OffsetDateTime endDate) {
    this.endDate = endDate;
  }

  public DocOrder refCarStateId(Integer refCarStateId) {
    this.refCarStateId = refCarStateId;
    return this;
  }

  /**
   * Get refCarStateId
   * @return refCarStateId
  **/
  @ApiModelProperty(value = "")

  public Integer getRefCarStateId() {
    return refCarStateId;
  }

  public void setRefCarStateId(Integer refCarStateId) {
    this.refCarStateId = refCarStateId;
  }

  public DocOrder refCarId(Integer refCarId) {
    this.refCarId = refCarId;
    return this;
  }

  /**
   * Get refCarId
   * @return refCarId
  **/
  @ApiModelProperty(value = "")

  public Integer getRefCarId() {
    return refCarId;
  }

  public void setRefCarId(Integer refCarId) {
    this.refCarId = refCarId;
  }

  public DocOrder sum(Integer sum) {
    this.sum = sum;
    return this;
  }

  /**
   * Get sum
   * @return sum
  **/
  @ApiModelProperty(value = "")

  public Integer getSum() {
    return sum;
  }

  public void setSum(Integer sum) {
    this.sum = sum;
  }

  public DocOrder createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
  **/
  @ApiModelProperty(value = "")

  @Valid
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public DocOrder updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
  **/
  @ApiModelProperty(value = "")

  @Valid
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
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
    DocOrder docOrder = (DocOrder) o;
    return Objects.equals(this.id, docOrder.id) &&
        Objects.equals(this.code, docOrder.code) &&
        Objects.equals(this.startDate, docOrder.startDate) &&
        Objects.equals(this.endDate, docOrder.endDate) &&
        Objects.equals(this.refCarStateId, docOrder.refCarStateId) &&
        Objects.equals(this.refCarId, docOrder.refCarId) &&
        Objects.equals(this.sum, docOrder.sum) &&
        Objects.equals(this.createdAt, docOrder.createdAt) &&
        Objects.equals(this.updatedAt, docOrder.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, startDate, endDate, refCarStateId, refCarId, sum, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DocOrder {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    refCarStateId: ").append(toIndentedString(refCarStateId)).append("\n");
    sb.append("    refCarId: ").append(toIndentedString(refCarId)).append("\n");
    sb.append("    sum: ").append(toIndentedString(sum)).append("\n");
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
