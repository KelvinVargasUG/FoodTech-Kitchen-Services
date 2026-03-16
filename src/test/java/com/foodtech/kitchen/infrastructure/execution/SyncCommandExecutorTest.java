package com.foodtech.kitchen.infrastructure.execution;

import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.commands.PrepareDrinkCommand;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("component")
class SyncCommandExecutorTest {

    private SyncCommandExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new SyncCommandExecutor();
    }

    @Test
    @DisplayName("Should execute single command")
    void shouldExecuteSingleCommand() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Command command = new PrepareDrinkCommand(List.of(cocaCola));

        // When & Then
        assertDoesNotThrow(() -> executor.execute(command));
    }

    @Test
    @DisplayName("Should execute multiple commands")
    void shouldExecuteMultipleCommands() {
        // Given
        Product cocaCola = new Product("Coca Cola", ProductType.DRINK, 5);
        Product sprite = new Product("Sprite", ProductType.DRINK, 5);
        
        Command command1 = new PrepareDrinkCommand(List.of(cocaCola));
        Command command2 = new PrepareDrinkCommand(List.of(sprite));
        
        List<Command> commands = List.of(command1, command2);

        // When & Then
        assertDoesNotThrow(() -> executor.executeAll(commands));
    }

    @Test
    @DisplayName("Should handle empty command list")
    void shouldHandleEmptyCommandList() {
        // Given
        List<Command> emptyCommands = List.of();

        // When & Then
        assertDoesNotThrow(() -> executor.executeAll(emptyCommands));
    }
}