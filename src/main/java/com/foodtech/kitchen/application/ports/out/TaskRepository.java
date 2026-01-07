package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.Station;

import java.util.List;

public interface TaskRepository {
    void saveAll(List<Task> tasks);
    List<Task> findByStation(Station station);
    List<Task> findAll();
}