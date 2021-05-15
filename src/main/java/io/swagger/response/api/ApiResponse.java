package io.swagger.response.api;

import lombok.Data;

@Data
public class ApiResponse {

    private String responseText;

    public ApiResponse(String responseText) {
        this.responseText = responseText;
    }
}
