package com.example.comp2100miniproject.src.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.comp2100miniproject.src.reactions.Reaction;
import com.example.comp2100miniproject.src.reactions.ReactionType;

public class ReactionDAO {
  private static ReactionDAO instance;
  private final List<Reaction> reactions;

  private ReactionDAO() {
    reactions = new ArrayList<>();
  }

  public static ReactionDAO getInstance() {
    if (instance == null) {
      instance = new ReactionDAO();
    }
    return instance;
  }

  public void addReaction(Reaction reaction) {
    reactions.add(reaction);
  }

  public void removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
    reactions.removeIf(
        reaction ->
            reaction.userUUID().equals(userUUID)
                && reaction.messageUUID().equals(messageUUID)
                && reaction.type().equals(type));
  }

  public List<Reaction> getReactions(UUID userUUID, UUID messageUUID) {
    return reactions.stream()
        .filter(
            reaction ->
                reaction.userUUID().equals(userUUID) && reaction.messageUUID().equals(messageUUID))
        .collect(Collectors.toList());
  }

  public List<Reaction> getAllReactions() {
    return new ArrayList<>(reactions);
  }

  public void clear() {
    reactions.clear();
  }
}
