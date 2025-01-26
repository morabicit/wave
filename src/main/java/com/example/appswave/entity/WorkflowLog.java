package com.example.appswave.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@Data
@Entity(name = "workflow_log")
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String logMessage;

    @Column(nullable = false, name = "created_date")
    private Date createdDate;

    public WorkflowLog(String logMessage, Date date) {
        this.logMessage = logMessage;
        this.createdDate = date;
    }
}
