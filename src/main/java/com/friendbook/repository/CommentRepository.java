package com.friendbook.repository;


import com.friendbook.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    void deleteByPostId(Integer postId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findAllCommentsByUserId(@Param("userId") Integer userId);
}
