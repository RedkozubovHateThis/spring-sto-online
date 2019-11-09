package io.swagger.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by phoenix on 01.11.17.
 */
public class SMSAPIResponse {
    private Long id;
    private Integer cnt;
    private Double cost;
    private Double balance;

    private String error;
    @JsonProperty("error_code")
    private Integer errorCode;

    public SMSAPIResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
