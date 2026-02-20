package com.foodtech.kitchen.domain.ports.out;

import com.foodtech.kitchen.domain.commands.Command;

// ARCHITECTURE_DECISION:
// This port abstracts asynchronous execution to prevent
// the application layer from depending on concurrency frameworks.
public interface AsyncCommandDispatcher {
    void dispatch(Command command, Long taskId);
}
