package com.example.appswave.workflow;


import com.example.appswave.entity.WorkflowLog;
import com.example.appswave.enums.Event;
import com.example.appswave.enums.Status;
import com.example.appswave.repository.WorkflowLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WorkflowStateMachineListener extends StateMachineListenerAdapter<Status, Event> {

    private final WorkflowLogRepository workflowLogRepository;

    private final StateMachineContextService stateMachineContextService;

    @Autowired
    public WorkflowStateMachineListener(WorkflowLogRepository workflowLogRepository,  StateMachineContextService stateMachineContextService) {
        this.workflowLogRepository = workflowLogRepository;
        this.stateMachineContextService = stateMachineContextService;
    }

    @Override
    public void stateChanged(State<Status, Event> from, State<Status, Event> to) {
        String logMessage = "State changed from " + (from != null ? from.getId() : "null") + " to " + to.getId();
        WorkflowLog log = new WorkflowLog(logMessage, new Date());
        workflowLogRepository.save(log);
    }

    @Override
    public void eventNotAccepted(Message<Event> event) {
        String logMessage = "Event not accepted: " + event.getPayload();
        WorkflowLog log = new WorkflowLog(logMessage, new Date());
        workflowLogRepository.save(log);
    }

    @Override
    public void transition(Transition<Status, Event> transition) {
        Event event = transition.getTrigger().getEvent();

        if (event != null) {

            Message<Event> eventMessage = stateMachineContextService.getMessage();

            if (eventMessage != null) {
                Long newsId = (Long) eventMessage.getHeaders().get("newsId");
                String logMessage = "Transition: Event " + event +
                        (newsId != null ? ", newsId: " + newsId : "") +
                        ", from " + (transition.getSource() != null ? transition.getSource().getId() : "null") +
                        " to " + (transition.getTarget() != null ? transition.getTarget().getId() : "null");

                WorkflowLog log = new WorkflowLog(logMessage, new Date());
                workflowLogRepository.save(log);
            } else {
                String logMessage = "Transition: Event " + event + ", no Message found ";
                WorkflowLog log = new WorkflowLog(logMessage, new Date());
                workflowLogRepository.save(log);
            }
        }
    }
}
