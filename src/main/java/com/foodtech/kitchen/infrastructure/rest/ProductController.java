package com.foodtech.kitchen.infrastructure.rest;

import com.foodtech.kitchen.application.ports.in.ChangeProductStatusPort;
import com.foodtech.kitchen.application.ports.in.CreateProductPort;
import com.foodtech.kitchen.application.ports.in.GetActiveProductsPort;
import com.foodtech.kitchen.application.ports.in.UpdateProductPort;
import com.foodtech.kitchen.application.usecases.dto.CreateProductCommand;
import com.foodtech.kitchen.application.usecases.dto.UpdateProductCommand;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateProductRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.CreateProductResponse;
import com.foodtech.kitchen.infrastructure.rest.dto.ProductResponse;
import com.foodtech.kitchen.infrastructure.rest.dto.UpdateProductRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.UpdateProductResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
@ConditionalOnProperty(name = "feature.product-catalog.enabled", havingValue = "true", matchIfMissing = false)
public class ProductController {

    private final CreateProductPort createProductPort;
    private final ChangeProductStatusPort changeProductStatusPort;
    private final GetActiveProductsPort getActiveProductsPort;
    private final UpdateProductPort updateProductPort;

    @GetMapping
    public ResponseEntity<List<CreateProductResponse>> getActiveProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name) {
        List<Product> products;
        if (name != null && !name.isBlank()) {
            products = getActiveProductsPort.executeByName(name);
        } else if (category != null && !category.isBlank()) {
            products = getActiveProductsPort.executeByCategory(category);
        } else {
            products = getActiveProductsPort.execute();
        }

        List<CreateProductResponse> response = products.stream()
                .map(p -> new CreateProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getType(),
                        p.getCategory(),
                        p.getPrice(),
                        p.getStatus()
                ))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
                request.getName(),
                request.getType(),
                request.getCategory(),
                request.getPrice()
        );

        Product product = createProductPort.execute(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        CreateProductResponse response = new CreateProductResponse(
                product.getId(),
                product.getName(),
                product.getType(),
                product.getCategory(),
                product.getPrice(),
                product.getStatus()
        );

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{uuid}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable UUID uuid) {
        Product product = changeProductStatusPort.deactivate(uuid);
        return ResponseEntity.ok(toProductResponse(product));
    }

    @PatchMapping("/{uuid}/activate")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable UUID uuid) {
        Product product = changeProductStatusPort.activate(uuid);
        return ResponseEntity.ok(toProductResponse(product));
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(), product.getName(), product.getType(),
                product.getCategory(), product.getPrice(), product.getStatus());
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UpdateProductResponse> updateProduct(
            @PathVariable UUID uuid,
            @RequestBody UpdateProductRequest request) {

        UpdateProductCommand command = new UpdateProductCommand(
                request.getName(),
                request.getDescription(),
                request.getType(),
                request.getCategory(),
                request.getPrice(),
                request.getStatus()
        );

        Product product = updateProductPort.execute(uuid, command);

        UpdateProductResponse response = new UpdateProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getType(),
                product.getCategory(),
                product.getPrice(),
                product.getStatus()
        );

        return ResponseEntity.ok(response);
    }
}
