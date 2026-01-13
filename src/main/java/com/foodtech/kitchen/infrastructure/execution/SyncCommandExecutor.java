package com.foodtech.kitchen.infrastructure.execution;

import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.domain.commands.Command;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SyncCommandExecutor implements CommandExecutor {

    @Override
    public void execute(Command command) {
        command.execute();
    }

    @Override
    public void executeAll(List<Command> commands) {
        commands.forEach(this::execute);
    }
}