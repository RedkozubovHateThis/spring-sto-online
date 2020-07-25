package io.swagger.postgres.repository;

import io.swagger.postgres.model.Customer;
import io.swagger.postgres.model.security.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    @Query("SELECT DISTINCT c FROM Customer AS c " +
            "WHERE lower(c.phone) LIKE lower(:phone) " +
            "OR lower(c.email) LIKE lower(:email) " +
            "OR lower(c.name) LIKE lower(:fio) " +
            "OR lower(c.inn) LIKE lower(:inn)")
    List<Customer> findAllByPhoneOrEmail(@Param("phone") String phone,
                                        @Param("email") String email,
                                        @Param("fio") String fio,
                                        @Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT c.id FROM customer AS c " +
            "WHERE c.phone = :phone )")
    Boolean isCustomerExistsPhone(@Param("phone") String phone);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT c.id FROM customer AS c " +
            "WHERE c.email = :email )")
    Boolean isCustomerExistsEmail(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT c.id FROM customer AS c " +
            "WHERE c.inn = :inn )")
    Boolean isCustomerExistsInn(@Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT c.id FROM customer AS c " +
            "WHERE c.inn = :inn AND c.id <> :userId )")
    Boolean isCustomerExistsInnNotSelf(@Param("inn") String inn,
                                      @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT c.id FROM customer AS c " +
            "WHERE c.phone = :phone AND c.id <> :userId )")
    Boolean isCustomerExistsPhoneNotSelf(@Param("phone") String phone,
                                        @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT c.id FROM customer AS c " +
            "WHERE c.email = :email AND c.id <> :userId )")
    Boolean isCustomerExistsEmailNotSelf(@Param("email") String email,
                                        @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT DISTINCT c.* FROM service_document AS sd\n" +
            "INNER JOIN customer AS c ON sd.customer_id = c.id AND c.deleted = FALSE\n" +
            "WHERE sd.executor_id = :executorId AND sd.deleted = FALSE")
    List<Customer> findCustomersByExecutorId(@Param("executorId") Long executorId);
}
