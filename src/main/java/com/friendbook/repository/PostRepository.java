package com.friendbook.repository;


import com.friendbook.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

	@Query("select p from Post p where p.user.id=?1")
	List<Post> findByUserId(Integer userId);

	@Query("SELECT p FROM Post p WHERE p.user.id IN :users ORDER BY p.createdAt DESC")
	List<Post> findAllPostByUserIds(@Param("users") List<Integer> userIds);

	@Query("SELECT p FROM Post p JOIN p.likedByUser l WHERE l.id = :userId")
	List<Post> findAllPostsLikedByUser(@Param("userId") Integer userId);

}
