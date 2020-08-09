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

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.id) FROM service_document AS sd WHERE sd.executor_id = :executorId AND sd.deleted = FALSE")
    Long countByExecutorId(@Param("executorId") Long executorId);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.id) FROM service_document AS sd WHERE sd.executor_id = :executorId AND sd.deleted = FALSE AND sd.status = :status")
    Long countByExecutorIdAndStatus(@Param("executorId") Long executorId,
                                    @Param("status") String status);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.id) FROM service_document AS sd WHERE sd.deleted = FALSE AND sd.status = :status")
    Long countByStatus(@Param("status") String status);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.id) FROM service_document AS sd WHERE sd.client_id = :clientId AND sd.deleted = FALSE")
    Long countByClientId(@Param("clientId") Long clientId);

    @Query(nativeQuery = true, value = "SELECT sum(sd.cost) FROM service_document AS sd WHERE sd.client_id = :clientId AND sd.deleted = FALSE")
    Double countTotalSumByClientId(@Param("clientId") Long clientId);

    @Query(nativeQuery = true, value = "SELECT sum(sd.cost) FROM service_document AS sd WHERE sd.executor_id = :executorId AND sd.deleted = FALSE")
    Double countTotalSumByExecutorId(@Param("executorId") Long executorId);

    @Query(nativeQuery = true, value = "SELECT sum(sd.cost) FROM service_document AS sd WHERE sd.deleted = FALSE")
    Double countTotalSum();

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.vehicle_id) FROM service_document AS sd WHERE sd.client_id = :clientId AND sd.deleted = FALSE AND sd.status = 'COMPLETED'")
    Long countVehiclesByClientId(@Param("clientId") Long clientId);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.vehicle_id) FROM service_document AS sd WHERE sd.executor_id = :executorId AND sd.deleted = FALSE AND sd.status = 'COMPLETED'")
    Long countVehiclesByExecutorId(@Param("executorId") Long executorId);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.vehicle_id) FROM service_document AS sd WHERE sd.deleted = FALSE AND sd.status = 'COMPLETED'")
    Long countVehicles();

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.executor_id) FROM service_document AS sd WHERE sd.client_id = :clientId AND sd.deleted = FALSE")
    Long countServicesByClientId(@Param("clientId") Long clientId);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.client_id) FROM service_document AS sd WHERE sd.executor_id = :executorId AND sd.deleted = FALSE")
    Long countClientsByExecutorId(@Param("executorId") Long clientId);

    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT sd.client_id) FROM service_document AS sd WHERE sd.deleted = FALSE")
    Long countClients();

    List<ServiceDocument> findByVehicleIdOrderByNumber(Long vehicleId);
    List<ServiceDocument> findByCustomerIdOrderByNumber(Long customerId);
}
