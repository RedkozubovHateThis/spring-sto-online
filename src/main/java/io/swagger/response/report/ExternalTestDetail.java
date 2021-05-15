package io.swagger.response.report;

import lombok.Data;

@Data
public class ExternalTestDetail {

    private Integer id;
    private Integer x;
    private Integer y;
    private String symbol;
    private Integer color;

    public Boolean isValid() {
        return id != null && x != null && x > 0 && y != null && y > 0 && symbol != null && symbol.length() > 0;
    }

}
