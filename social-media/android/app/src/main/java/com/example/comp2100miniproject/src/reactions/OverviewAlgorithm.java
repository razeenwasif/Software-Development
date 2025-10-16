package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.model.Message;

import java.util.*;

public class OverviewAlgorithm implements IReactionReporter{

    public OverviewAlgorithm() {

    }

    public ReactionDisplayTag[] generateReport(Message message) {
        // Get all reactions of a message
        List<Reaction> allReactions = ReactionsFacade.getAllReactions(message);

        LinkedHashMap<ReactionType, List<Reaction>> userReactionHashMap = new LinkedHashMap<>();
        for (Reaction reaction : allReactions) {
            if (!userReactionHashMap.containsKey(reaction.type())) {
                userReactionHashMap.put(reaction.type(), new ArrayList<>());
            }
            userReactionHashMap.get(reaction.type()).add(reaction);
        }

        int returnListSize = Math.min(userReactionHashMap.size(), 5);
        ReactionDisplayTag[] return_list = new ReactionDisplayTag[returnListSize];

        int count = 0;
        for (ReactionType reactionType : userReactionHashMap.keySet()) {
            if (count == returnListSize)
                break;
            List<Reaction> reactionList = userReactionHashMap.get(reactionType);
            return_list[count] = new ReactionDisplayTag(reactionType, String.valueOf(reactionList.size()));
            count++;
        }
        List<ReactionType> reactionTypeList = new ArrayList<>(userReactionHashMap.keySet());
        List<ReactionDisplayTag> sortedList = Arrays.stream(return_list).sorted((o1, o2) -> o2.label().compareTo(o1.label()) != 0 ?
                                                                                o2.label().compareTo(o1.label()) :
                                                                                (reactionTypeList.indexOf(o1.type()) > reactionTypeList.indexOf(o2.type()) ? 1 : -1))
                                                                        .toList();

        for (int i = 0; i < sortedList.size(); i++) {
            return_list[i] = sortedList.get(i);
        }

        return return_list;
    }

}
