package com.example.comp2100miniproject.src.dao.model;

import java.util.UUID;

public record User(UUID id, Role role, String username, String password) implements HasUUID {
	public enum Role {Member, Admin}

	public UUID getUUID() {
		return id;
	}

	public User(UUID id) {
		this(id, Role.Member, null, null);
	}

	public User(String username) {
		this(null, Role.Member, username, null);
	}
}
