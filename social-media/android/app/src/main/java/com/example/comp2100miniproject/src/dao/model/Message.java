package com.example.comp2100miniproject.src.dao.model;

import java.util.UUID;

public record Message(UUID id, UUID poster, UUID thread, long timestamp, String message) {

    public UUID getMessageUUID()
    {
        return id;
    }

    public UUID getPosterUUID()
    {
        return poster;
    }

    public String getMessage()
    {
        return message;
    }

    public long getTimestamp()
    {
        return timestamp;
    }


}