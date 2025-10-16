package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.model.Message;

public interface IReactionReporter {
	public ReactionDisplayTag[] generateReport(Message message);
}
