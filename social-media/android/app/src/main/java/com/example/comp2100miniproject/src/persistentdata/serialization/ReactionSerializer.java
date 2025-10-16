package com.example.comp2100miniproject.src.persistentdata.serialization;

import java.util.UUID;
import com.example.comp2100miniproject.src.reactions.Reaction;
import com.example.comp2100miniproject.src.reactions.ReactionType;

public class ReactionSerializer implements Serializer<Reaction, String> {

  @Override
  public String serialize(Reaction reaction) {
    // example: "uuid, messageuid, type, timestamp"
    return String.join(
        ",",
        reaction.userUUID().toString(),
        reaction.messageUUID().toString(),
        reaction.type().name(),
        String.valueOf(reaction.timestamp()));
  }

  @Override
  public Reaction deserialize(String data) {
    String[] parts = data.split(",");
    return new Reaction(
        UUID.fromString(parts[0]),
        UUID.fromString(parts[1]),
        UUID.fromString(parts[2]),
        ReactionType.valueOf(parts[3]),
        Long.parseLong(parts[4]));
  }
}
