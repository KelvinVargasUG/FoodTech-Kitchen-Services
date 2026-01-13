package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.commands.Command;

import java.util.List;

public interface CommandExecutor {
    void execute(Command command);
    void executeAll(List<Command> commands);
}