package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.model.Post;
import com.example.comp2100miniproject.src.dao.model.User;

import com.example.comp2100miniproject.src.dao.PostDAO;
import com.example.comp2100miniproject.src.reactions.ReactionDAO;
import com.example.comp2100miniproject.src.dao.UserDAO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.example.comp2100miniproject.src.dao.model.Message;
import com.example.comp2100miniproject.src.dao.model.Post;

import java.util.*;
import java.util.List;
import java.util.UUID;
import com.example.comp2100miniproject.src.persistentdata.PersistentDataException;
import com.example.comp2100miniproject.src.persistentdata.serialization.ReactionSerializer;

public class ReactionsFacade {
	/**
	 * Adds a reaction by a particular user of a particular type to a particular message.
	 * Returns true if the reaction was successfully added, and false otherwise.
	 * Users may have an arbitrary number of reactions on a single message, but only one of a given type.
	 */
	public static boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        Reaction reaction = new Reaction(UUID.randomUUID(), userUUID, messageUUID, type, timestamp);

        boolean success = ReactionDAO.getInstance().add(reaction);
        if (success) {
            savePersistentData();
        }
        return success;
	}

	/**
	 * Removes a reaction by a particular user of a particular type to a particular message.
	 * Returns true if the reaction was successfully removed, and false otherwise.
	 */
	public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {

        Reaction forSearch = new Reaction(UUID.randomUUID(), userUUID, messageUUID, type, 0);

        Reaction reaction = ReactionDAO.getInstance().get(forSearch);

        if (reaction == null)
            return false;

        boolean success = ReactionDAO.getInstance().delete(reaction);
        if (success) {
            savePersistentData();
        }
        return success;
	}
  private static final String REACTIONS_FILE = "reactions.csv";

	/**
	 * Fetches all reactions made by a particular user on a particular message.
	 * Returns null if either userUUID or messageUUID do not correspond to actual User or Message.
	 * They must be returned in chronological (time-based) order, from oldest to newest.
	 */
	public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {

        List<Reaction> resultList = new ArrayList<>();
        List<ReactionType> result = new ArrayList<>();

        for (Iterator<Reaction> it = ReactionDAO.getInstance().getAll(); it.hasNext(); ) {
            Reaction reaction = it.next();
            if (reaction.userUUID().equals(userUUID) && reaction.messageUUID().equals(messageUUID) )
                resultList.add(reaction);
        }

        return sortReactionsByTime(resultList);
	}

	public static List<Reaction> getAllReactions(Message message) {
		List<Reaction> reactionList = new ArrayList<>();
		for (Iterator<Reaction> reactionIterator = ReactionDAO.getInstance().getAll(); reactionIterator.hasNext(); ) {
			Reaction reaction = reactionIterator.next();
			if (reaction.messageUUID().equals(message.id())) {
				reactionList.add(reaction);
			}
		}

        reactionList.sort(Comparator.comparingLong(Reaction::timestamp));
		return  reactionList;
	}

	private static List<ReactionType> sortReactionsByTime(List<Reaction> list) {

        List<ReactionType> result = new ArrayList<>();

        Set<ReactionType> uniques = new HashSet<>();

        List<Reaction> temp = new ArrayList<>(list);
        temp.sort(Comparator.comparing(Reaction::timestamp));

        for (Reaction r : temp) {
            if (!uniques.contains(r.type())) {
                uniques.add(r.type());
                result.add(r.type());
            }
        }

        return result;
    }

    /**
     * Loads all persistent data (users, messages, posts, and importantly reactions) from persistent
     * data.
     */
    public static void loadPersistentData() throws PersistentDataException {
        UserDAO.getInstance().clear();
        PostDAO.getInstance().clear();
        ReactionDAO.getInstance().clear();

        // Load users and posts

        ReactionDAO reactionDAO = ReactionDAO.getInstance();
        ReactionSerializer serializer = new ReactionSerializer();

        try (BufferedReader reader = new BufferedReader(new FileReader(REACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reactionDAO.add(serializer.deserialize(line));
            }
        } catch (IOException e) {
            // File might not exist on first run, so ignore
        }
    }

    /** Saves all reaction data to persistent storage. */
    public static void savePersistentData() {
        ReactionDAO reactionDAO = ReactionDAO.getInstance();
        ReactionSerializer serializer = new ReactionSerializer();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REACTIONS_FILE))) {
            for (Iterator<Reaction> reactionIterator = reactionDAO.getAll(); reactionIterator.hasNext(); ) {
                writer.write(serializer.serialize(reactionIterator.next()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistentDataException(e.getMessage());
        }
    }
}
