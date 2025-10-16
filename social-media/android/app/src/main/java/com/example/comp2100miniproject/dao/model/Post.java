package com.example.comp2100miniproject.dao.model;

import com.example.comp2100miniproject.dao.MessageComparator;
import com.example.comp2100miniproject.sorteddata.SortedData;
import com.example.comp2100miniproject.sorteddata.SortedDataFactory;

import java.util.UUID;

public class Post implements HasUUID {
  public final UUID id;
  public final UUID poster;
  public final String topic;
  /** The tag associated with the post, used for filtering. */
  public final String tag;
  public final SortedData<Message> messages;

  public Post(UUID id, UUID poster, String topic, String tag) {
    this.id = id;
    this.poster = poster;
    this.topic = topic;
    this.tag = tag;
    this.messages = SortedDataFactory.makeSortedData(MessageComparator.getInstance());
  }

  public Post(UUID id, UUID poster, String topic) {
    this(id, poster, topic, null);
  }

  public Post(UUID id) {
    this(id, null, null, null);
  }

  public UUID getUUID() {
    return id;
  }
}
