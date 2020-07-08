package io.swagger.postgres.repository;

import io.swagger.postgres.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT DISTINCT c FROM Customer AS c " +
            "WHERE lower(c.phone) LIKE lower(:phone) " +
            "OR lower(c.email) LIKE lower(:email) " +
            "OR lower(c.name) LIKE lower(:fio) " +
            "OR lower(c.inn) LIKE lower(:inn)")
    List<Customer> findAllByPhoneOrEmail(@Param("phone") String phone,
                                        @Param("email") String email,
                                        @Param("fio") String fio,
                                        @Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.phone = :phone )")
    Boolean isCustomerExistsPhone(@Param("phone") String phone);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.email = :email )")
    Boolean isCustomerExistsEmail(@Param("email") String email);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.inn = :inn )")
    Boolean isCustomerExistsInn(@Param("inn") String inn);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.inn = :inn AND p.id <> :userId )")
    Boolean isCustomerExistsInnNotSelf(@Param("inn") String inn,
                                      @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.phone = :phone AND p.id <> :userId )")
    Boolean isCustomerExistsPhoneNotSelf(@Param("phone") String phone,
                                        @Param("userId") Long userId);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT p.id FROM profile AS p " +
            "WHERE p.email = :email AND p.id <> :userId )")
    Boolean isCustomerExistsEmailNotSelf(@Param("email") String email,
                                        @Param("userId") Long userId);
}
