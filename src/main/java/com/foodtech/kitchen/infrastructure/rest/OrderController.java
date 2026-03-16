package com.foodtech.kitchen.infrastructure.rest;

import com.foodtech.kitchen.application.ports.in.GetCompletedOrdersPort;
import com.foodtech.kitchen.application.ports.in.GetOrderStatusPort;
import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.application.ports.in.RequestOrderInvoicePort;
import com.foodtech.kitchen.application.ports.in.DeleteOrderPort;
import com.foodtech.kitchen.application.usecases.dto.CompletedOrderView;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;
import com.foodtech.kitchen.infrastructure.rest.dto.CompletedOrderResponse;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateOrderResponse;
import com.foodtech.kitchen.infrastructure.rest.mapper.CompletedOrderMapper;
import com.foodtech.kitchen.infrastructure.rest.mapper.OrderMapper;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final String ORDER_SUCCESS_MESSAGE = "Order processed successfully";

    private final ProcessOrderPort processOrderPort;
    private final GetOrderStatusPort getOrderStatusPort;
    private final GetCompletedOrdersPort getCompletedOrdersPort;
    private final RequestOrderInvoicePort requestOrderInvoicePort;
    private final DeleteOrderPort deleteOrderPort;


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

    @GetMapping("/{orderId}/status")
    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable Long orderId) {
        TaskStatus status = getOrderStatusPort.execute(orderId);
        return ResponseEntity.ok(Map.of(
                "orderId", orderId.toString(),
                "status", status.name()
        ));
    }

    @GetMapping("/completed")
    public ResponseEntity<List<CompletedOrderResponse>> getCompletedOrders() {
        List<CompletedOrderView> views = getCompletedOrdersPort.execute();
        return ResponseEntity.ok(CompletedOrderMapper.toResponseList(views));
    }

    @PostMapping("/{orderId}/invoice")
    public ResponseEntity<Void> requestInvoice(@PathVariable Long orderId) {
        requestOrderInvoicePort.execute(orderId);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        deleteOrderPort.execute(orderId);
        return ResponseEntity.noContent().build();
    }
}