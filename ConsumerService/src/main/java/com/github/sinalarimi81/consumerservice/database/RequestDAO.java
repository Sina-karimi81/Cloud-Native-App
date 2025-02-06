package com.github.sinalarimi81.consumerservice.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestDAO extends JpaRepository<RequestEntity, Long> {

    @Query(value = "select r from RequestEntity r where r.status = :status")
    List<RequestEntity> getAllByStatus(String status);
}
