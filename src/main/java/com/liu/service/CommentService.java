package com.liu.service;

import com.liu.dao.CommentDAO;
import com.liu.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;
    public int addComment(Comment comment) {
        return commentDAO.addComment(comment);
    }
    public List<Comment> selectComment(int entityId,int entityType)
    {
        return commentDAO.selectByEntityId(entityId,entityType,1);
    }
    public int deleteComment(Comment comment)
    {
        return commentDAO.deleteComment(comment.getId(),0);
    }
    public int getComment(int entityId,int entityType)
    {
        return commentDAO.getCommentCount(entityId,entityType,1);
    }

    public Comment getCommentById(int commentId) {
        return commentDAO.getComment(commentId);
    }

    public int getUserCommentCount(int usrId) {
        return commentDAO.getUserCommentCount(usrId);
    }
    public List<Comment> getCommentByUserId(int userId) {
        return commentDAO.selectCommetByUerId(userId);
    }
}
