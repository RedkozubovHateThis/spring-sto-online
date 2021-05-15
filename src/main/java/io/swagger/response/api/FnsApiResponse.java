package io.swagger.response.api;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FnsApiResponse {
    private List<Map<String, Object>> suggestions;
}
