package com.example.appswave.workflow;

import com.example.appswave.enums.Event;
import com.example.appswave.enums.Status;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

@Service
public class StateMachineContextService {

    public final StateMachine<Status, Event> stateMachine;

    public StateMachineContextService(StateMachine<Status, Event> stateMachine) {
        this.stateMachine = stateMachine;
    }

    public Message<Event> getMessage() {
        return stateMachine.getExtendedState().get(Message.class, Message.class);
    }

}
