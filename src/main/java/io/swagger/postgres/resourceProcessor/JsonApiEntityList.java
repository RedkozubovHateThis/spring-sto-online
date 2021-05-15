package io.swagger.postgres.resourceProcessor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class JsonApiEntityList {
    private List<JsonApiData> data = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<JsonApiData> included = new ArrayList<>();
    private JsonApiListMeta meta = new JsonApiListMeta();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> links = new HashMap<>();

    public void putLink(String name, String link) {
        links.put(name, link);
    }

    public void addData(JsonApiData jsonApiData) {
        data.add(jsonApiData);
    }
}
