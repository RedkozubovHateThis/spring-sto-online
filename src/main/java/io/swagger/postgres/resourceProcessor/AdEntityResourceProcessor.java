package io.swagger.postgres.resourceProcessor;

import io.swagger.postgres.model.AdEntity;
import io.swagger.postgres.model.Vehicle;
import io.swagger.postgres.model.security.Profile;
import io.swagger.postgres.model.security.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdEntityResourceProcessor extends SimpleResourceProcessor<AdEntity> {

    @Value("${crnk.domain-name}")
    private String domainName;

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public void customAttributes(Map<String, Object> attributes, AdEntity adEntity) {
        if ( adEntity.getServiceLeader() != null ) {
            attributes.put("addedById", adEntity.getServiceLeader().getId());

            User serviceLeader = adEntity.getServiceLeader();
            if ( serviceLeader.getFio() != null && serviceLeader.getFio().length() > 0 )
                attributes.put("addedBy", adEntity.getServiceLeader().getFio());
            else if ( serviceLeader.getProfile() != null ) {
                Profile profile = serviceLeader.getProfile();

                if ( profile.getName() != null && profile.getName().length() > 0 )
                    attributes.put("addedBy", profile.getName());
                else if ( profile.getEmail() != null && profile.getEmail().length() > 0 )
                    attributes.put("addedBy", profile.getEmail());
                else if ( profile.getPhone() != null && profile.getPhone().length() > 0 )
                    attributes.put("addedBy", profile.getPhone());
                else
                    attributes.put("addedBy", serviceLeader.getUsername());
            }
        }
    }
}
