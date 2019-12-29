package io.swagger.config.database;

import io.swagger.postgres.model.enums.SubscriptionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class SubscriptionTypeConverter implements AttributeConverter<SubscriptionType, String> {
    @Override
    public String convertToDatabaseColumn(SubscriptionType attribute) {
        return attribute.name();
    }

    @Override
    public SubscriptionType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(SubscriptionType.values())
                .filter(c -> c.name().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
