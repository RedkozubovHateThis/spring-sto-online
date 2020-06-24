package io.swagger.postgres.resourceProcessor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class JsonApiData {
    private String id;
    private String type;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> links = new HashMap<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> attributes = new HashMap<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> relationships = new HashMap<>();

    public void putLink(String name, String link) {
        links.put(name, link);
    }

    public void putAttribute(String name, Object attribute) {
        attributes.put(name, attribute);
    }

    public void putRelation(String name, Object relation) {
        relationships.put(name, relation);
    }

    public Boolean isSame(JsonApiData data) {
        return id.equals( data.id ) && type.equals( data.getType() );
    }
}
