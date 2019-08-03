package io.swagger.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


@Table(name = "DOCUMENT_ACTION")
public class DocumentAction {
    private int DOCUMENT_ACTION_ID;
    private int DOCUMENT_REGISTRY_ID;
    private int USER_ID;
    private Date ACTION_DATETIME;
    private int STATE;
    private double SUMMA;

    public DocumentAction(int DOCUMENT_ACTION_ID, int DOCUMENT_REGISTRY_ID,
                          int USER_ID,
                          Date ACTION_DATETIME,
                          int STATE,
                          double SUMMA) {


        this.DOCUMENT_ACTION_ID = DOCUMENT_ACTION_ID;
        this.DOCUMENT_REGISTRY_ID = DOCUMENT_REGISTRY_ID;
        this.USER_ID = USER_ID;
        this.ACTION_DATETIME = ACTION_DATETIME;
        this.STATE = STATE;
        this.SUMMA = SUMMA;

    }

    public int getDOCUMENT_ACTION_ID() {
        return DOCUMENT_ACTION_ID;
    }

    public void setDOCUMENT_ACTION_ID(int DOCUMENT_ACTION_ID) {
        this.DOCUMENT_ACTION_ID = DOCUMENT_ACTION_ID;
    }

    public int getDOCUMENT_REGISTRY_ID() {
        return DOCUMENT_REGISTRY_ID;
    }

    public void setDOCUMENT_REGISTRY_ID(int DOCUMENT_REGISTRY_ID) {
        this.DOCUMENT_REGISTRY_ID = DOCUMENT_REGISTRY_ID;
    }

    public int getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(int USER_ID) {
        this.USER_ID = USER_ID;
    }

    public Date getACTION_DATETIME() {
        return ACTION_DATETIME;
    }

    public void setACTION_DATETIME(Date ACTION_DATETIME) {
        this.ACTION_DATETIME = ACTION_DATETIME;
    }

    public int getSTATE() {
        return STATE;
    }

    public void setSTATE(int STATE) {
        this.STATE = STATE;
    }

    public double getSUMMA() {
        return SUMMA;
    }

    public void setSUMMA(double SUMMA) {
        this.SUMMA = SUMMA;
    }

    @Override
    public String toString() {
        return String.format(
                "DocumentAction[DOCUMENT_ACTION_ID=%d, DOCUMENT_REGISTRY_ID='%d', " +
                        "USER_ID='%d', ACTION_DATETIME='%s',STATE='%d',SUMMA='%s']",
                DOCUMENT_ACTION_ID, DOCUMENT_REGISTRY_ID, USER_ID,ACTION_DATETIME,STATE,SUMMA);
    }

}
