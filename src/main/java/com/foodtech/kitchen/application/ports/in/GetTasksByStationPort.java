package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;

import java.util.List;

public interface GetTasksByStationPort {
    // ARCHITECTURE_DECISION:
    // This input port centralizes filtering logic in the application layer.
    // REST adapters must not access persistence directly.
    List<Task> execute(Station station, TaskStatus status);
}
