package com.foodtech.kitchen.domain.ports.out;

import com.foodtech.kitchen.domain.commands.Command;

public interface AsyncCommandDispatcher {
    void dispatch(Command command, Long taskId);
}
