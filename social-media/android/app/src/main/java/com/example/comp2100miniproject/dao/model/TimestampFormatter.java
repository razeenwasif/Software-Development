package com.example.comp2100miniproject.dao.model;

// This is an interface for different timestamp formatting strategies.
public interface TimestampFormatter {
    // This is an instance method that implementing classes will provide.
    String format(long timestamp);
}
