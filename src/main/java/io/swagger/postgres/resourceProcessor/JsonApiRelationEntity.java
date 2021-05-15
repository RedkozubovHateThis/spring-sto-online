package io.swagger.postgres.resourceProcessor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class JsonApiRelationEntity {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object data = new HashMap<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> links = new HashMap<>();

    public void putLink(String name, String link) {
        links.put(name, link);
    }
}
