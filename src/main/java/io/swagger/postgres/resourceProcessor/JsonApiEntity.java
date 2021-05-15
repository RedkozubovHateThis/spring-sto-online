package io.swagger.postgres.resourceProcessor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class JsonApiEntity {
    private JsonApiData data;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<JsonApiData> included = new ArrayList<>();
}
