package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.*;
import com.foodtech.kitchen.infrastructure.persistence.jpa.TaskJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.TaskEntity;
import com.foodtech.kitchen.infrastructure.persistence.mappers.TaskEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//HUMAN REVIEW: Simplifiqué adapter inyectando TaskEntityMapper dedicado.
//Cumple SRP: este adapter solo adapta entre JPA y dominio, mapper maneja serialización.
//Elimina duplicación: ProductDto y lógica JSON centralizados en mapper.
@Component
public class TaskRepositoryAdapter implements TaskRepository {

    private final TaskJpaRepository jpaRepository;
    private final TaskEntityMapper mapper;

    public TaskRepositoryAdapter(TaskJpaRepository jpaRepository, TaskEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<Task> tasks) {
        List<TaskEntity> entities = tasks.stream()
            .map(mapper::toEntity)
            .collect(Collectors.toList());
        
        jpaRepository.saveAll(entities);
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = mapper.toEntity(task);
        TaskEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpaRepository.findByIdWithProducts(id) // ✅ Usar eager fetch
                .map(mapper::toDomain);
    }

    @Override
    public List<Task> findByStation(Station station) {
        return jpaRepository.findByStation(station).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByStationAndStatus(Station station, TaskStatus status) {
        return jpaRepository.findByStationAndStatus(station, status).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}