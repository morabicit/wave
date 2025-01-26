package com.example.appswave.workflow;

import com.example.appswave.enums.Event;

public class WorkflowEvent {
    private final Event event;
    private final Long newsId;

    public WorkflowEvent(Event event, Long newsId) {
        this.event = event;
        this.newsId = newsId;
    }

    public Event getEvent() {
        return event;
    }

    public Long getNewsId() {
        return newsId;
    }
}

