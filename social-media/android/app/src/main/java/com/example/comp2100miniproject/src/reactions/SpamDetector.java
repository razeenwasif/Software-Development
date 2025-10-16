package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.PostDAO;
import com.example.comp2100miniproject.src.dao.model.Message;
import com.example.comp2100miniproject.src.dao.model.*;

import java.util.*;

public class SpamDetector {
// algorithm to check whether a user might be spamming reactions (true) or not spamming (false)
public boolean checkSpamForUser(User user) {
	Iterator<Message> messageIterator = PostDAO.getInstance().getAllMessages();
	float probability = 0;
	List<UUID> across = new ArrayList<UUID>();
	while (messageIterator.hasNext()) {
		// Note to self: it's inefficient to search through all the messages and users when we really only need to check any that have changed since the last time this function was called, but there's no way to do this with the current codebase... Maybe once architectural decisions have been made, it'll be possible to do something about this. Also, we expect this to be called regularly, so it might also be worth looking into caching results from each execution of the function to speed it up in subsequent invokations.
		Message message = messageIterator.next();
		int[] frequency = new int[ReactionType.values().length];
		for (ReactionDisplayTag displayTag : ReactionReportFactory.buildReporter("overview").generateReport(message)) {
            try {
                frequency[displayTag.type().ordinal()] += Integer.parseInt(displayTag.label());
            }
            catch (NumberFormatException ignored) {

            };
        }

        if (across.stream().noneMatch(x -> x.equals(message.id()))) {
            for (ReactionType type : ReactionsFacade.getReactions(user.getUUID(), message.id())) {
                probability = probability + 1f / (frequency[type.ordinal()] > 3 ? 3 : frequency[type.ordinal()]);
                across.add(message.thread());
            }
        }
    }

	return ((probability * across.size())) >= 5; // 5 is the threshold. We can tweak it during testing.
}}