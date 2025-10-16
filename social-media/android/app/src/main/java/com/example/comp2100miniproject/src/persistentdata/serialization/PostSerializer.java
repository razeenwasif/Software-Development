package com.example.comp2100miniproject.src.persistentdata.serialization;
import com.example.comp2100miniproject.src.dao.model.Post;

import java.util.UUID;

/**
 * Converts between Posts and String[] by converting each field of Post
 * (UUID, poster, and topic) to a string, which becomes one of the entries
 * within the array
 */
public class PostSerializer implements Serializer<Post, String[]> {

	@Override
	public String[] serialize(Post object) {
		String topic = object.topic == null ? "" : object.topic;
		String tag = object.tag == null ? "" : object.tag;
		return new String[] {object.id.toString(), object.poster.toString(), topic, tag};
	}

	@Override
	public Post deserialize(String[] data) {
		String topic = data.length > 2 ? data[2] : "";
		String tag = data.length > 3 ? data[3] : null;
		return new Post(UUID.fromString(data[0]), UUID.fromString(data[1]), topic, tag);
	}
}
