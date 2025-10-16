package com.example.comp2100miniproject.reactions;

import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.User;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user reaction left on a particular message, recording who reacted, with which type,
 * and when the reaction occurred. No external code was copied when implementing this model.
 *
 * @author u7283652
 */
public class Reaction {
    private final UUID id;
    private final User user;
    private final Message message;
    private final ReactionType type;
    private final long timestamp;

    /**
     * Constructs a reaction with an auto-generated identifier and timestamp.
     *
     * @param user     reacting user
     * @param message  message that received the reaction
     * @param type     category of reaction
     */
    public Reaction(User user, Message message, ReactionType type) {
        this(UUID.randomUUID(), user, message, type, System.currentTimeMillis());
    }

    /**
     * Constructs a fully specified reaction, used when hydrating from storage or seeding data.
     *
     * @param id        persistent identifier
     * @param user      reacting user
     * @param message   message that received the reaction
     * @param type      category of reaction
     * @param timestamp epoch timestamp of the reaction in milliseconds
     */
    public Reaction(UUID id, User user, Message message, ReactionType type, long timestamp) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
    }

    /**
     * @return the unique identifier for this reaction
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return the user who posted the reaction
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the message that was reacted to
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @return the type of reaction
     */
    public ReactionType getType() {
        return type;
    }

    /**
     * @return timestamp of the reaction in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reaction reaction = (Reaction) o;
        return Objects.equals(user, reaction.user)
                && Objects.equals(message, reaction.message)
                && type == reaction.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, message, type);
    }
}
