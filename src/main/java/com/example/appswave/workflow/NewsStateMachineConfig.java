package com.example.appswave.workflow;

import com.example.appswave.enums.Event;
import com.example.appswave.enums.Status;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
public class NewsStateMachineConfig extends StateMachineConfigurerAdapter<Status, Event> {
    private final WorkflowStateMachineListener workflowStateMachineListener;

    public NewsStateMachineConfig(WorkflowStateMachineListener workflowStateMachineListener) {
        this.workflowStateMachineListener = workflowStateMachineListener;
    }


    @Override
    public void configure(StateMachineStateConfigurer<Status, Event> states) throws Exception {
        states
                .withStates()
                .initial(Status.PENDING)
                .states(EnumSet.allOf(Status.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<Status, Event> transitions) throws Exception {
        transitions
                .withExternal()
                .source(Status.PENDING).target(Status.APPROVED).event(Event.APPROVE)
                .and()
                .withExternal()
                .source(Status.PENDING).target(Status.DELETED).event(Event.DELETE)
                .and()
                .withExternal()
                .source(Status.APPROVED).target(Status.PENDING_DELETION).event(Event.DELETE) // Request deletion
                .and()
                .withExternal()
                .source(Status.PENDING_DELETION).target(Status.DELETED).event(Event.DELETE) // Admin approves deletion
                .and()
                .withExternal()
                .source(Status.PENDING_DELETION).target(Status.APPROVED).event(Event.APPROVE); // Admin rejects deletion
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<Status, Event> config) throws Exception {
        config
                .withConfiguration()
                .listener(workflowStateMachineListener);
    }
}
