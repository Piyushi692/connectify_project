package com.friendbook.service;



import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;

import java.util.List;

public interface PostService {

	 Post createPost(Post post, UserModel user) throws UserException;


	 void deletePost(Integer postId, UserModel user) throws UserException, PostException;

	 List<Post> findPostByUserId(Integer userId) throws UserException;

	 Post findPostById(Integer postId) throws PostException;

	 List<Post> findAllPostByUserIds(List<Integer> userIds) throws PostException;

	 Post likePost(Integer postId, UserModel currentUser) throws PostException, UserException;

	 Post unlikePost(Integer postId, UserModel user) throws PostException;

	Integer getPostCountByUser(UserModel viewedUser) throws UserException;

	 List<Post> findAllPostsLikedByUser(UserModel user);
}
