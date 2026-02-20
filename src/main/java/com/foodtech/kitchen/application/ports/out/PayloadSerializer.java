package com.foodtech.kitchen.application.ports.out;

import java.util.Map;

public interface PayloadSerializer {
    String serialize(Map<String, Object> payload);
}
