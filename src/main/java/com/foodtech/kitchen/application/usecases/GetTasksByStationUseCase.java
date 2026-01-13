package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.in.GetTasksByStationPort;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Station;
import com.foodtech.kitchen.domain.model.Task;

import java.util.List;

public class GetTasksByStationUseCase implements GetTasksByStationPort {

    private final TaskRepository taskRepository;

    public GetTasksByStationUseCase(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> execute(Station station) {
        return taskRepository.findByStation(station);
    }
}
