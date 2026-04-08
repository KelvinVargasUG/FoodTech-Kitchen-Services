package com.foodtech.kitchen.application.exepcions;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class ChunkAssemblyExceptionTest {

    @Test
    void constructor_storesMessageAndCause() {
        RuntimeException cause = new RuntimeException("root cause");
        ChunkAssemblyException ex = new ChunkAssemblyException("assembly failed", cause);

        assertThat(ex.getMessage()).isEqualTo("assembly failed");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    void isRuntimeException() {
        ChunkAssemblyException ex = new ChunkAssemblyException("msg", new Exception("cause"));

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
