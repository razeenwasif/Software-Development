package com.example.comp2100miniproject.src.reactions;


public record ReactionDisplayTag(ReactionType type, String label) {

    public ReactionType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }


}