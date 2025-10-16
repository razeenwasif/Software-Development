package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.UserDAO;
import com.example.comp2100miniproject.src.dao.model.Message;
import com.example.comp2100miniproject.src.dao.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OldestAlgorithm implements IReactionReporter{

    public OldestAlgorithm() {

    }


    public ReactionDisplayTag[] generateReport(Message message) {
        //get all reactions
        List<Reaction> allReactions = ReactionsFacade.getAllReactions(message);

        HashMap<UUID, Reaction> userReactionHashMap = new HashMap<>();

        for (Reaction reaction : allReactions) {
            if (!userReactionHashMap.containsKey(reaction.userUUID())) {
                userReactionHashMap.put(reaction.userUUID(), reaction);
            }
        }

        int returnListSize = Math.min(userReactionHashMap.size(), 5);
        ReactionDisplayTag[] return_list = new ReactionDisplayTag[returnListSize];
        int count = 0;
        for (UUID userID : userReactionHashMap.keySet()) {
            if (count == returnListSize)
                break;
            User user = UserDAO.getInstance().getByUUID(userID);
            Reaction reaction = userReactionHashMap.get(userID);
            return_list[count] = new ReactionDisplayTag(reaction.type(), user.username());
            count++;
        }
        return return_list;
    }


}
