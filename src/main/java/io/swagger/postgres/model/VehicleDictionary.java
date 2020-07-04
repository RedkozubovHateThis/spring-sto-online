package io.swagger.postgres.model;

import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;

@EqualsAndHashCode(of = "id", callSuper = true)
@Data
@Entity
@Where(clause = "deleted=false")
@JsonApiResource(type = "vehicleDictionary", resourcePath = "vehicleDictionaries")
public class VehicleDictionary extends BaseDictionaryEntity {
}
