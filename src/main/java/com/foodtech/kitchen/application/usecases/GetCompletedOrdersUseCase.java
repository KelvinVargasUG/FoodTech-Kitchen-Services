package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.in.GetCompletedOrdersPort;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.usecases.dto.CompletedOrderView;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Task;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GetCompletedOrdersUseCase implements GetCompletedOrdersPort {

    private final OrderRepository orderRepository;
    private final TaskRepository taskRepository;

    public GetCompletedOrdersUseCase(OrderRepository orderRepository, TaskRepository taskRepository) {
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<CompletedOrderView> execute() {
        List<Order> orders = orderRepository.findByStatus(OrderStatus.COMPLETED);
        return orders.stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    private CompletedOrderView toView(Order order) {
        List<Task> tasks = taskRepository.findByOrderId(order.getId());
        LocalDateTime completedAt = resolveCompletedAt(tasks);
        Long totalPreparationTime = resolveTotalPreparationTime(tasks, completedAt);

        return new CompletedOrderView(
                order.getId(),
                order.getTableNumber(),
                completedAt,
                order.getProducts().size(),
                totalPreparationTime
        );
    }

    private LocalDateTime resolveCompletedAt(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getCompletedAt)
                .filter(value -> value != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private Long resolveTotalPreparationTime(List<Task> tasks, LocalDateTime completedAt) {
        if (completedAt == null) {
            return null;
        }

        Optional<LocalDateTime> earliestStart = tasks.stream()
                .map(Task::getStartedAt)
                .filter(value -> value != null)
                .min(Comparator.naturalOrder());

        if (earliestStart.isEmpty()) {
            return null;
        }

        return Duration.between(earliestStart.get(), completedAt).toSeconds();
    }
}
