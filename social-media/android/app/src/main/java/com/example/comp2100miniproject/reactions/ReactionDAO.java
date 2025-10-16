package com.example.comp2100miniproject.reactions;

import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.User;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Simple in-memory DAO that stores reactions keyed by message. All logic was authored locally
 * without referencing third-party sources.
 *
 * @author u7283652
 */
public class ReactionDAO {

    private static ReactionDAO instance;

    /**
     * Obtains the singleton instance for the DAO, creating one as needed.
     *
     * @return shared {@link ReactionDAO} instance
     */
    public static ReactionDAO getInstance() {
        if (instance == null) {
            instance = new ReactionDAO();
        }
        return instance;
    }

    /** Cache of reactions grouped by message identifier. */
    private final Map<UUID, List<Reaction>> reactionsByMessage = new ConcurrentHashMap<>();

    private ReactionDAO() {
    }

    /**
     * Adds a new reaction entry for the associated message.
     *
     * @param reaction reaction to record
     */
    public void addReaction(Reaction reaction) {
        if (reaction == null || reaction.getMessage() == null) {
            return;
        }
        UUID messageId = reaction.getMessage().id();
        reactionsByMessage
                .computeIfAbsent(messageId, id -> new ArrayList<>())
                .add(reaction);
    }

    /**
     * Provides the list of reactions for a message.
     *
     * @param message message whose reactions are required
     * @return immutable view of reactions; empty when none exist
     */
    public List<Reaction> getReactions(Message message) {
        if (message == null) {
            return List.of();
        }
        return reactionsByMessage.getOrDefault(message.id(), List.of());
    }

    /**
     * Provides the list of reactions for a message identifier.
     *
     * @param messageId identifier for the message
     * @return list of reactions for the message
     */
    public List<Reaction> getReactions(UUID messageId) {
        if (messageId == null) {
            return List.of();
        }
        return reactionsByMessage.getOrDefault(messageId, List.of());
    }

    /**
     * Summarises reactions for the given message by type.
     *
     * @param message message whose reactions should be counted
     * @return map of reaction types to their occurrence counts
     */
    public Map<ReactionType, Long> getReactionCounts(Message message) {
        return getReactions(message).stream()
                .collect(Collectors.groupingBy(Reaction::getType,
                        () -> new EnumMap<>(ReactionType.class),
                        Collectors.counting()));
    }

    /**
     * Summarises reactions for the given message identifier by type.
     *
     * @param messageId identifier whose reactions should be counted
     * @return map of reaction types to their occurrence counts
     */
    public Map<ReactionType, Long> getReactionCounts(UUID messageId) {
        return getReactions(messageId).stream()
                .collect(Collectors.groupingBy(Reaction::getType,
                        () -> new EnumMap<>(ReactionType.class),
                        Collectors.counting()));
    }

    /** Retrieves all reactions stored across every message. */
    public List<Reaction> getAllReactions() {
        List<Reaction> all = new ArrayList<>();
        reactionsByMessage.values().forEach(all::addAll);
        return all;
    }

    /**
     * Removes all reactions currently tracked for the supplied message.
     *
     * @param message message whose reactions should be cleared
     */
    public void removeReactions(Message message) {
        if (message == null) {
            return;
        }
        reactionsByMessage.remove(message.id());
    }

    /** Clears all recorded reactions. */
    public void clear() {
        reactionsByMessage.clear();
    }

    /**
     * Seeds pseudo-random reactions from a set of candidate users.
     *
     * @param message      message to react to
     * @param participants possible users contributing a reaction
     */
    public void seedRandomReactions(Message message, List<User> participants) {
        if (message == null || participants == null || participants.isEmpty()) {
            return;
        }
        for (User user : participants) {
            if (user == null) {
                continue;
            }
            ReactionType randomType = ReactionType.values()[(int) (Math.random() * ReactionType.values().length)];
            addReaction(new Reaction(user, message, randomType));
        }
    }
}
