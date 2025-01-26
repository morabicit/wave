package com.example.appswave.workflow;

import com.example.appswave.enums.Event;
import com.example.appswave.enums.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends StateMachineConfigurerAdapter<Status, Event> {
    @Bean
    public StateMachineListener<Status, Event> stateMachineListener(WorkflowStateMachineListener listener) {
        return listener;
    }
    @Override
    public void configure(StateMachineStateConfigurer<Status, Event> states) throws Exception {
        states.withStates()
                .initial(Status.PENDING)
                .state(Status.APPROVED)
                .state(Status.PENDING_DELETION)
                .state(Status.DELETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<Status, Event> transitions) throws Exception {
        transitions.withExternal()
                .source(Status.PENDING).target(Status.APPROVED).event(Event.APPROVE)
                .and()
                .withExternal()
                .source(Status.APPROVED).target(Status.PENDING_DELETION).event(Event.DELETE)
                .and()
                .withExternal()
                .source(Status.PENDING_DELETION).target(Status.DELETED).event(Event.DELETE)
                .and()
                .withExternal()
                .source(Status.PENDING_DELETION).target(Status.APPROVED).event(Event.APPROVE);
    }
}
