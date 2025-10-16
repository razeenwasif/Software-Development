package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.model.HasUUID;

import java.util.UUID;

public record Reaction(UUID id, UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) implements HasUUID {
    @Override
    public UUID getUUID() {
        return id;
    }

    // It would be better to collect all pairs (ReactionType type, long timestamp) in List<Pair<ReactionType, Long>>:
    // public record Reaction(UUID id, UUID userUUID, UUID messageUUID, List<Pair<ReactionType, Long>> typeAndTimestamp) ...

}