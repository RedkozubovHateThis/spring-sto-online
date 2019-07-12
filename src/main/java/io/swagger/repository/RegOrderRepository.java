package io.swagger.repository;

import io.swagger.model.RefUser;
import io.swagger.model.RegOrder;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegOrderRepository extends PagingAndSortingRepository<RegOrder, Integer> {

    List<RegOrder> findAll();

}
