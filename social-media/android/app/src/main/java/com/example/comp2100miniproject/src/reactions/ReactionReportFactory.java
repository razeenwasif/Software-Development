package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.model.Message;

public class ReactionReportFactory {


	public static IReactionReporter buildReporter(String type) {
		// TODO: task 2
        if(type.equalsIgnoreCase("oldest"))
		{
			return new OldestAlgorithm();
		}
		else if(type.equalsIgnoreCase("overview"))
		{
			return new OverviewAlgorithm();
		}

		return null;
	}





}
