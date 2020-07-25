package io.swagger.postgres.repository;

import io.swagger.postgres.model.ServiceDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceDocumentRepository extends JpaRepository<ServiceDocument, Long>, JpaSpecificationExecutor<ServiceDocument> {

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT sd.id FROM service_document AS sd " +
            "WHERE sd.number = :number )")
    Boolean isServiceDocumentExistsNumber(@Param("number") String number);

    @Query(nativeQuery = true, value = "SELECT EXISTS( SELECT sd.id FROM service_document AS sd " +
            "WHERE sd.number = :number AND sd.id <> :id )")
    Boolean isServiceDocumentExistsNumberNotSelf(@Param("number") String number,
                                                 @Param("id") Long id);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.id) FROM service_document AS sd WHERE sd.executor_id = :executorId")
    Long countAllByExecutorId(@Param("executorId") Long executorId);

    List<ServiceDocument> findByVehicleIdOrderByNumber(Long vehicleId);
    List<ServiceDocument> findByCustomerIdOrderByNumber(Long customerId);
}
