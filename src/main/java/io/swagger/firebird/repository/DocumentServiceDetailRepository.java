package io.swagger.firebird.repository;

import io.swagger.firebird.model.DocumentServiceDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentServiceDetailRepository extends PagingAndSortingRepository<DocumentServiceDetail, Integer> {

    Page<DocumentServiceDetail> findAll(Pageable pageable);

}
