package com.example.comp2100miniproject.dao;

import com.example.comp2100miniproject.dao.model.HasUUID;
import com.example.comp2100miniproject.dao.model.Message;
import com.example.comp2100miniproject.dao.model.Post;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID; // Added import for UUID

public class PostDAO extends DAO<Post> {
	/**
	 * Generates a PostDAO by automatically building a Comparator that
	 * checks just that the UUID fields match. If you don't understand
	 * this syntax, don't worry. It's an advanced Java technique.
	 */
	private PostDAO() {
		super(Comparator.comparing(HasUUID::getUUID));
	}
	private static PostDAO instance;

	/**
	 * Gets a singleton instance of PostDAO, creating one if necessary.
	 * @return the instance
	 */
	public static PostDAO getInstance() {
		if (instance == null) instance = new PostDAO();
		return instance;
	}

	/**
	 * Gets a post by its UUID.
	 * @param postId The UUID of the post to retrieve.
	 * @return The Post object if found, null otherwise.
	 */
	public Post getByUUID(UUID postId) {
		// Create a temporary Post object with the desired UUID to use with the inherited get() method.
		// The Post class has a constructor public Post(UUID id) which can be used here.
		Post searchKey = new Post(postId);
		return get(searchKey); // Calls the get(T element) method from the DAO superclass
	}

	/**
	 * Returns all posts authored by the supplied user identifier.
	 *
	 * @param userId identifier of the poster
	 * @return list of matching posts
	 */
	public java.util.List<Post> getPostsByUser(UUID userId) {
		java.util.List<Post> posts = new java.util.ArrayList<>();
		if (userId == null) {
			return posts;
		}
		Iterator<Post> iterator = getAll();
		while (iterator.hasNext()) {
			Post post = iterator.next();
			if (userId.equals(post.poster)) {
				posts.add(post);
			}
		}
		return posts;
	}

	/**
	 * Gets the ith post, in order of timestamp
	 * @param i the index of the post to search for
	 * @return the post
	 */
	public Post getAtIndex(int i) {
		return data.getAtIndex(i);
	}

	/**
	 * Returns an Iterator that iterates through every message given as a reply to
	 * every post stored within the DAO, in no particular order.
	 * @return the iterator
	 */
	public Iterator<Message> getAllMessages() {
		// Iterator<Message> result = null;
		// TODO: Complete this method using the Iterator design pattern
        return new AllMessagesIterator();
	}

	/**
	 * Performs a linear search across posts to locate a message by identifier.
	 *
	 * @param messageId identifier to match
	 * @return message when found, otherwise {@code null}
	 */
	public Message findMessageById(UUID messageId) {
        if (messageId == null) {
            return null;
        }
        Iterator<Post> postsIterator = getAll();
        while (postsIterator.hasNext()) {
            Post post = postsIterator.next();
            Iterator<Message> messageIterator = post.messages.getAll();
            while (messageIterator.hasNext()) {
                Message message = messageIterator.next();
                if (messageId.equals(message.id())) {
                    return message;
                }
            }
        }
        return null;
    }

    private static class AllMessagesIterator implements Iterator<Message> {

        private int postIndex = 0;
        private int messageIndex = 0;
        private Message nextMessage;

        public AllMessagesIterator() {
            // Prime the iterator by finding the very first available message.
            findNext();
        }

        @Override
        public boolean hasNext() {
            return nextMessage != null;
        }

        @Override
        public Message next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more messages to iterate over.");
            }
            Message messageToReturn = nextMessage;
            findNext(); // Find the next message for the *next* call to next().
            return messageToReturn;
        }

        private void findNext() {
            // loop will continue until we either find a message or run out of posts.
            while (true) {
                Post currentPost;
                try {
                    // Try to get the current post.
                    currentPost = PostDAO.getInstance().getAtIndex(postIndex);
                } catch (IndexOutOfBoundsException e) {
                    // If this fails, there are no more posts. The iteration is complete.
                    this.nextMessage = null;
                    return;
                }

                try {
                    // Try to get a message from the current post at the current message index.

                    // Success! We found a message. Store it and prepare for the next message search.
                    this.nextMessage = currentPost.messages.getAtIndex(messageIndex);
                    this.messageIndex++; // Advance the message index for this post.
                    return; // Exit, we have found what we need for this call.

                } catch (IndexOutOfBoundsException e) {
                    // This means we've exhausted all messages in the current post.
                    // We must now move to the next post.
                    this.postIndex++;
                    this.messageIndex = 0; // Reset the message index for the new post.
                    // The loop will now continue and try again with the new postIndex.
                }
            }
        }
    }

}
