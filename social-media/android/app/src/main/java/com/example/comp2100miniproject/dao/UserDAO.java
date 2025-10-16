package com.example.comp2100miniproject.dao;

import com.example.comp2100miniproject.dao.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class UserDAO extends DAO<User> {
	// apply the Singleton design pattern to this class.
	// You may modify the existing constructor, add new constructors,
	// and add new helper method and private fields.
	/**
	 * Generates a UserDAO. We enforce uniqueness in usernames (but not in passwords),
	 * and further two usernames are considered identical if they are equal, ignoring case
	 */
    private static volatile UserDAO instance;
	private UserDAO() {
		super((o1, o2) -> o1.username().compareToIgnoreCase(o2.username()));
	}
	public static UserDAO getInstance() {

        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
	}

	/**
	 * Attempts to authenticate as a particular user. If the user exists
	 * and their passwords match, the login is considered successful.
	 * @param username the username
	 * @param password the password
	 * @return the User if successful, null otherwise
	 */
	public User login(String username, String password) {
		// TODO: Complete this method, interfacing with the DAO pattern, to the specification in the javadoc
		// create temp user obj with username to search for
        User userToFind = new User(username); // DAO comparator only uses username
        // find user
        User storedUser = super.get(userToFind); // inherited get() from DAO superclass
        if (storedUser != null && storedUser.password().equals(password)) {
            return storedUser;
        }
        return null;
	}

	/**
	 * Attempts to register a new user. Users must have unique usernames,
	 * and their usernames must contain only alphanumeric characters.
	 * Usernames can be between 4 and 20 characters long.
	 * Passwords must be at least four characters long, and can include
	 * any codepoints.
	 * @param username the desired username
	 * @param password the desired password
	 * @return the newly-created User if successful, null otherwise
	 */
	public User register(String username, String password) {
		// TODO: Complete this method, interfacing with the DAO pattern, to the specification in the javadoc
		if (username == null || username.length() < 4 || username.length() > 20) { return null; }
        if (password == null || password.length() < 4) { return null; }
        if (!username.matches("^[a-zA-Z0-9]+$")) { return null; }
        if (super.get(new User(username)) != null) { return null; } // if get returns user, username already taken
        User newUser = new User(
                UUID.randomUUID(),      // Generate a new, non-null UUID
                User.Role.Member,       // Registered users should be members
                username,
                password
        );

        // insert new user into data store using inherited insert
        super.add(newUser);

        return newUser;
	}

	/**
	 * Fetches a User by just a UUID
	 * @param id the UUID to search for
	 * @return the user if they exist, else null
	 */
	public User getByUUID(UUID id) {
        for (Iterator<User> it = data.getAll(); it.hasNext(); ) {
            User user = it.next();
            if (user.getUUID().equals(id)) return user;
        }
		return null;
	}

	/**
	 * Performs a case-insensitive substring search across usernames.
	 *
	 * @param query partial username to match
	 * @return list of users whose usernames contain the query
	 */
	public List<User> searchByUsername(String query) {
		List<User> results = new ArrayList<>();
		if (query == null) {
			return results;
		}
		String lowerQuery = query.toLowerCase();
		Iterator<User> iterator = data.getAll();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (user.username() != null && user.username().toLowerCase().contains(lowerQuery)) {
				results.add(user);
			}
		}
		return results;
	}
}
