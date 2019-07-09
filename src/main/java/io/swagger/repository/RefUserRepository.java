package io.swagger.repository;

import io.swagger.model.RefUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefUserRepository extends PagingAndSortingRepository<RefUser, Integer> {

    List<RefUser> findAll();

}
