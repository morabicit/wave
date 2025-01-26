package com.example.appswave.repository;


import com.example.appswave.entity.News;
import com.example.appswave.entity.User;
import com.example.appswave.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByStatus(Status status);

    Optional<Object> findByIdAndStatusNot(Long id, Status status);

    List<News> findByCreatedBy(User createdBy);

    @Modifying
    @Query("UPDATE News n SET n.status = :status WHERE n.publishDate < :currentDate AND n.status <> :status")
    void updateExpiredNewsStatus(@Param("currentDate") Date currentDate, @Param("status") Status status);
    Optional<Object> findByIdAndStatus(Long id, Status status);

    List<News> findByStatusIn(List<Status> status);

    List<News> findByCreatedByAndStatusNot(User createdBy, Status status);
}