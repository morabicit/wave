package com.example.appswave.service;

import com.example.appswave.entity.News;
import com.example.appswave.enums.Status;
import com.example.appswave.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NewsSchedulerService {


    private final NewsRepository newsRepository;

    public NewsSchedulerService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Cron expression for daily at midnight
    //@Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void autoSoftDeleteExpiredNews() {
        newsRepository.updateExpiredNewsStatus(new Date(), Status.DELETED);
    }
}
