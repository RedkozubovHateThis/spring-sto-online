package io.swagger.postgres.repository;

import io.swagger.postgres.model.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    UploadFile findOneByUuid(String uuid);

}
