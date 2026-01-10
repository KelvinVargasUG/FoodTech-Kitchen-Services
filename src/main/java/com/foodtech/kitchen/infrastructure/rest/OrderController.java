package com.foodtech.kitchen.infrastructure.rest;

import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderResponse;
import com.foodtech.kitchen.infrastructure.rest.mapper.OrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//HUMAN REVIEW: Simplificado controller para cumplir SRP. Solo coordina: mapeo, ejecución, respuesta.
//Manejo de errores delegado a GlobalExceptionHandler. Eliminado OrderResponseFactory (sobre-ingeniería/YAGNI).
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final String ORDER_SUCCESS_MESSAGE = "Order processed successfully";
    
    private final ProcessOrderPort processOrderPort;

    public OrderController(ProcessOrderPort processOrderPort) {
        this.processOrderPort = processOrderPort;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = OrderMapper.toDomain(request);
        List<Task> tasks = processOrderPort.execute(order);
        CreateOrderResponse response = new CreateOrderResponse(
            order.getTableNumber(),
            tasks.size(),
            ORDER_SUCCESS_MESSAGE
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}