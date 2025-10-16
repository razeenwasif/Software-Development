package com.example.comp2100miniproject.src.reactions;

import com.example.comp2100miniproject.src.dao.DAO;

public class ReactionDAO  extends DAO<Reaction> {

    public ReactionDAO() {
        super((o1, o2) -> o1.userUUID().compareTo(o2.userUUID()) != 0 ?
                o1.userUUID().compareTo(o2.userUUID()) :
                (o1.messageUUID().compareTo(o2.messageUUID())) != 0 ?
                        o1.messageUUID().compareTo(o2.messageUUID()) :
                        o1.type().compareTo(o2.type()) );

    }

    private static ReactionDAO instance;

    public static ReactionDAO getInstance() {
        if (instance == null) instance = new ReactionDAO();
        return instance;
    }


}
