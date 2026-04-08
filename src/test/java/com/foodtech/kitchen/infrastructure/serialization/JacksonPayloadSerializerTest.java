package com.foodtech.kitchen.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class JacksonPayloadSerializerTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JacksonPayloadSerializer serializer;

    @Test
    void serialize_whenObjectMapperSucceeds_returnsJson() throws Exception {

        Map<String, Object> payload = Map.of("k", "v");
        when(objectMapper.writeValueAsString(payload)).thenReturn("{\"k\":\"v\"}");

        String result = serializer.serialize(payload);

        assertEquals("{\"k\":\"v\"}", result);
    }

    @Test
    void serialize_whenObjectMapperFails_throwsIllegalStateException() throws Exception {

        Map<String, Object> payload = Map.of("k", "v");
        when(objectMapper.writeValueAsString(payload))
                .thenThrow(new JsonProcessingException("boom") {});

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> serializer.serialize(payload));
        assertNotNull(ex.getCause());
        assertEquals("Failed to serialize payload", ex.getMessage());
    }
}
