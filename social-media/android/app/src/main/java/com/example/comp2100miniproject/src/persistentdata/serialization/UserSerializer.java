package com.example.comp2100miniproject.src.persistentdata.serialization;

import com.example.comp2100miniproject.src.dao.model.User;

import java.util.UUID;

/**
 * TODO: Document your schema here
 */
public class UserSerializer implements Serializer<User, String[]> {
	@Override
	public String[] serialize(User object) {
		return new String[] {object.id().toString(), serialize(object.role()), object.username(), object.password()};
	}

	@Override
	public User deserialize(String[] data) {
		return new User(UUID.fromString(data[0]), deserialize(data[1]), data[2], data[3]);
	}

	private static String serialize(User.Role role) {
		return switch (role) {
			case Member -> "member";
			case Admin -> "admin";
		};
	}

	private static User.Role deserialize(String role) {
		return switch (role) {
			case "member" -> User.Role.Member;
			case "admin" -> User.Role.Admin;
			default -> throw new RuntimeException();
		};
	}
}
