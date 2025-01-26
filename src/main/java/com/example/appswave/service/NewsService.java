package com.example.appswave.service;


import com.example.appswave.entity.News;
import com.example.appswave.entity.User;
import com.example.appswave.enums.Event;
import com.example.appswave.enums.Role;
import com.example.appswave.enums.Status;
import com.example.appswave.repository.NewsRepository;
import com.example.appswave.repository.UserRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final StateMachine<Status, Event> stateMachine;

    public NewsService(NewsRepository newsRepository, UserRepository userRepository, StateMachine<Status, Event> stateMachine) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.stateMachine = stateMachine;
    }


    public News createNews(News news) throws AccessDeniedException {
        User createdBy = getCurrentUser();
        news.setStatus(Status.PENDING);
        news.setCreatedBy(createdBy);
        return newsRepository.save(news);
    }

    public List<News> getAllNews() throws AccessDeniedException {
        User createdBy = getCurrentUser();
        if (createdBy.getRole() == Role.USER) {
            return newsRepository.findByStatusIn(List.of(Status.APPROVED, Status.PENDING_DELETION));
        } else if (createdBy.getRole() == Role.WRITER) {
            return newsRepository.findByCreatedByAndStatusNot(createdBy, Status.DELETED);
        }
        return newsRepository.findAll();
    }
    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
    }

    public void deleteNews(Long id) throws AccessDeniedException {
        News news = (News) newsRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("News not found or already deleted with id: " + id));
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN && !news.getCreatedBy().equals(currentUser)) {
            throw new AccessDeniedException("You are not authorized to delete this news");
        }
        Status oldStatus = news.getStatus();
        if (currentUser.getRole() == Role.ADMIN) {
            if (news.getStatus() == Status.PENDING) {
                newsRepository.deleteById(id);
            } else if (news.getStatus() == Status.APPROVED || news.getStatus() == Status.PENDING_DELETION) {
                news.setStatus(Status.DELETED);
                newsRepository.save(news);
            }
        } else if (currentUser.getRole() == Role.WRITER) {
            if (news.getStatus() == Status.PENDING) {
                newsRepository.deleteById(id);
            }else if (news.getStatus() == Status.APPROVED) {
                news.setStatus(Status.PENDING_DELETION);
                newsRepository.save(news);
            } else {
                throw new AccessDeniedException("News already pending for deletion");
            }
        }
        initMachines(news, oldStatus);
    }

    public String approveNews(Long id){
        String message = "News approved successfully";
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        if (news.getStatus() != Status.PENDING) {
            message = "News is already approved";
            return message;
        }
        Status oldStatus= news.getStatus();
        news.setStatus(Status.APPROVED);
        newsRepository.save(news);
        initMachines(news ,oldStatus);
        return message;
    }
    public List<News> getPendingNews() {
        return newsRepository.findByStatus(Status.PENDING);
    }

    public News partialUpdateNews(Long id, Map<String, Object> updates) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        if (!currentUser.getRole().equals(Role.ADMIN) && news.getStatus() != Status.PENDING) {
            throw new AccessDeniedException("Only Admin can update approved news");
        }
        if(currentUser.getRole().equals(Role.WRITER) && !currentUser.getId().equals(news.getCreatedBy().getId())) {
            throw new AccessDeniedException("You can only update your own news");
        }
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(News.class, key);
            Objects.requireNonNull(field).setAccessible(true);
            ReflectionUtils.setField(field, news, value);
        });
    return newsRepository.save(news);
    }
    public List<News> getPendingDeleteNews() {
        return newsRepository.findByStatus(Status.PENDING_DELETION);
    }
    public String approveDeleteNews(Long id) {
        News news = (News) newsRepository.findByIdAndStatus(id, Status.PENDING_DELETION)
                .orElseThrow(() -> new RuntimeException("No pending for deletion news found with id : " + id));
        Status oldStatus = news.getStatus();
        news.setStatus(Status.DELETED);
        newsRepository.save(news);
        initMachines(news, oldStatus);
        return "Deletion request approved. News deleted successfully.";
    }

    public String rejectDeleteNews(Long id) {
        News news = (News) newsRepository.findByIdAndStatus(id, Status.PENDING_DELETION)
                .orElseThrow(() -> new RuntimeException("No pending for deletion news found with id : " + id));
        Status oldStatus = news.getStatus();
        news.setStatus(Status.APPROVED);
        newsRepository.save(news);
        initMachines(news, oldStatus);
        return "Deletion request rejected. News is still approved.";
    }

    private User getCurrentUser() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("No authenticated user found");
        }
        return  userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }
    public void initMachines(News news,Status oldStatus) {
        stateMachine.stop();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> access.resetStateMachine(
                        new DefaultStateMachineContext<>(oldStatus, null, null, null)));

        stateMachine.start();
        Message<Event> eventMessage = MessageBuilder.withPayload(Event.APPROVE)
                .setHeader("newsId", news.getId())
                .build();
        stateMachine.sendEvent(eventMessage);
    }
}
