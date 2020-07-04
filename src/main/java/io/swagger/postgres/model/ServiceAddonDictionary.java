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
@JsonApiResource(type = "serviceAddonDictionary", resourcePath = "serviceAddonDictionaries")
public class ServiceAddonDictionary extends BaseDictionaryEntity {
}
