package io.swagger.postgres.repository;

import io.swagger.postgres.model.payment.SubscriptionAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SubscriptionAddonRepository extends JpaRepository<SubscriptionAddon, Long> {
}
