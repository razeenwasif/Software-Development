package com.example.comp2100miniproject.dao.model;

import java.util.UUID;

public record Message(UUID id, UUID poster, UUID thread, long timestamp, String message) implements HasUUID {
    @Override
    public UUID getUUID() {
        return this.id;
    }
}