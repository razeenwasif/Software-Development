package com.example.comp2100miniproject.reactions;

/**
 * Enumerates the supported reaction types that a user can leave on a message and provides an emoji
 * representation for quick display within the UI.
 * @author u7283652
 */
public enum ReactionType {
    LIKE("👍"),
    HAPPY("😊"),
    SURPRISE("😮"),
    ANGRY("😡"),
    LAUGH("😂"),
    SAD("😢"),
    LOVE("❤️"),
    GOOD_LUCK("🍀"),
    CONGRATULATIONS("🎉");

    private final String emoji;

    ReactionType(String emoji) {
        this.emoji = emoji;
    }

    /**
     * Returns the emoji representation for the reaction.
     *
     * @return emoji string for the reaction
     */
    public String getEmoji() {
        return emoji;
    }
}
