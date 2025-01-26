package com.example.appswave.repository;


import com.example.appswave.entity.WorkflowLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowLogRepository extends JpaRepository<WorkflowLog, Long> {
}
