package com.example.comp2100miniproject.reactions;

/**
 * Immutable projection that couples a {@link ReactionType} with the number of occurrences for
 * display purposes within the UI. Implementation authored in-house.
 * @author u7283652
 */
public record ReactionSummary(ReactionType type, long count) { }
