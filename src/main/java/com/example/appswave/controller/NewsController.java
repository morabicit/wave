package com.example.appswave.controller;


import com.example.appswave.entity.News;
import com.example.appswave.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // Create a new news item (Content Writer only)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WRITER')")
    public ResponseEntity<News> createNews(@Valid @RequestBody News news) {
        News createdNews = newsService.createNews(news);
        return ResponseEntity.status(201).body(createdNews);
    }

    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        List<News> newsList = newsService.getAllNews();
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        return ResponseEntity.ok().body(newsService.getNewsById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WRITER')")
    public ResponseEntity<News> updateNews(@PathVariable Long id,@RequestBody Map<String, Object> newsParams) throws AccessDeniedException {
        News updatedNews = newsService.partialUpdateNews(id, newsParams);
        return ResponseEntity.ok(updatedNews);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WRITER')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) throws AccessDeniedException {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.approveNews(id));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<News>> getPendingNews() {
        List<News> pendingNews = newsService.getPendingNews();
        return ResponseEntity.ok(pendingNews);
    }

    @GetMapping("/pending-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<News>> getPendingDeleteNews() {
        List<News> pendingDeleteNews = newsService.getPendingDeleteNews();
        return ResponseEntity.ok(pendingDeleteNews);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve-delete")
    public String approveDeleteNews(@PathVariable Long id) {
        return newsService.approveDeleteNews(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject-delete")
    public String rejectDeleteNews(@PathVariable Long id) {
        return newsService.rejectDeleteNews(id);
    }
}
