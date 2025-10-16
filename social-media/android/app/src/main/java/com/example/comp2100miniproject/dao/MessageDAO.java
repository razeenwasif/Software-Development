package com.example.comp2100miniproject.dao;

import com.example.comp2100miniproject.dao.model.Message;

public class MessageDAO extends DAO<Message> {

    private static MessageDAO instance;
    
    private MessageDAO() {
        super(MessageComparator.getInstance());
    }
    
    public static MessageDAO getInstance() {
        if (instance == null) {
            instance = new MessageDAO();
        }
        return instance;
    }

    public Message getAtIndex(int index) {
        return data.getAtIndex(index);
    }
}