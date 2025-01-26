package com.example.appswave.controller;


import com.example.appswave.entity.News;
import com.example.appswave.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private NewsService newsService;

    // Create a new news item (Content Writer only)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WRITER')")
    public ResponseEntity<News> createNews(@Valid @RequestBody News news) throws AccessDeniedException {
        News createdNews = newsService.createNews(news);
        return ResponseEntity.status(201).body(createdNews);
    }

    // Get all news items
    @GetMapping
    public ResponseEntity<List<News>> getAllNews() throws AccessDeniedException {
        List<News> newsList = newsService.getAllNews();
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        return ResponseEntity.ok().body(newsService.getNewsById(id));
    }
    // Update news (Content Writer or admin)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WRITER')")
    public ResponseEntity<News> updateNews(@PathVariable Long id,@RequestBody Map<String, Object> newsParams) throws AccessDeniedException {
        News updatedNews = newsService.partialUpdateNews(id, newsParams);
        return ResponseEntity.ok(updatedNews);
    }

    // Delete news (Content Writer or Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','WRITER')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) throws AccessDeniedException {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.approveNews(id));
    }

    // Get pending news (Admin only)
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<News>> getPendingNews() {
        List<News> pendingNews = newsService.getPendingNews();
        return ResponseEntity.ok(pendingNews);
    }
    //admin get pending delete news
    @GetMapping("/pending-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<News>> getPendingDeleteNews() {
        List<News> pendingDeleteNews = newsService.getPendingDeleteNews();
        return ResponseEntity.ok(pendingDeleteNews);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve-delete")
    public String approveDeleteNews(@PathVariable Long id) {
        return newsService.approveDeleteNews(id);
    }
    //rejectDeleteNews
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject-delete")
    public String rejectDeleteNews(@PathVariable Long id) {
        return newsService.rejectDeleteNews(id);
    }
}
