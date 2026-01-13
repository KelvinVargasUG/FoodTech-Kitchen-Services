package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.domain.model.Task;

public interface StartTaskPreparationPort {
    Task execute(Long taskId);
}
