package com.foodtech.kitchen.infrastructure.rest.dto;

public class ChunkResponse {
    private String uploadId;
    private int chunkIndex;
    private boolean received;

    public ChunkResponse() {}
    public ChunkResponse(String uploadId, int chunkIndex, boolean received) {
        this.uploadId = uploadId;
        this.chunkIndex = chunkIndex;
        this.received = received;
    }
    public String getUploadId() { return uploadId; }
    public int getChunkIndex() { return chunkIndex; }
    public boolean isReceived() { return received; }
}
