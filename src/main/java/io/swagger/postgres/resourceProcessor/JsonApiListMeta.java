package io.swagger.postgres.resourceProcessor;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.crnk.core.resource.meta.MetaInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonApiListMeta implements MetaInformation {

    @JsonProperty(value = "total_resources")
    private Long totalResources;
}
