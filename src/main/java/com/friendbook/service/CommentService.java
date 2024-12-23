package com.friendbook.service;


import com.friendbook.Exception.CommentException;
import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entity.Comment;
import com.friendbook.entity.UserModel;

import java.util.List;

public interface CommentService {
    Comment createComment(Comment comment, Integer postId, UserModel user) throws PostException;

    void deleteComment(Integer commentId, Integer currentUserId) throws PostException, UserException;

    List<Comment> getCommentsByPostId(int postId) throws   PostException;
}
