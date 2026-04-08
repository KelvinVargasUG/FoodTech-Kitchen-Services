package com.foodtech.kitchen.application.exepcions;

public class FileSizeLimitExceededException extends RuntimeException {

    private final long actualSizeBytes;
    private final long maxSizeBytes;

    public FileSizeLimitExceededException(long actualSizeBytes, long maxSizeBytes) {
        super(String.format(
                "El archivo ensamblado (%d bytes) supera el tamaño máximo permitido (%d bytes, aproximadamente 5 GB).",
                actualSizeBytes, maxSizeBytes));
        this.actualSizeBytes = actualSizeBytes;
        this.maxSizeBytes = maxSizeBytes;
    }

    public long getActualSizeBytes() {
        return actualSizeBytes;
    }

    public long getMaxSizeBytes() {
        return maxSizeBytes;
    }
}
