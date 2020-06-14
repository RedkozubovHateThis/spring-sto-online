package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.SubscriptionAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionAddonRepository extends JpaRepository<SubscriptionAddon, Long> {
}
