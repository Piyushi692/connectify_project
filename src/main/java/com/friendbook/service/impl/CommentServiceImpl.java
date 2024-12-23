package com.friendbook.service.impl;

import com.friendbook.Exception.CommentException;
import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entity.Comment;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;
import com.friendbook.repository.CommentRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.service.CommentService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;
import com.friendbook.dto.UserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PostService postService;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private NotificationService notificationService;

	@Override
	public Comment createComment(Comment comment, Integer postId, UserModel user) throws PostException {

		Post post = postService.findPostById(postId);

		UserDto userDto = new UserDto(user.getId(),user.getUsername(),user.getEmail(), user.getName(), user.getImage(),user.getMobile(),user.getBio());
		comment.setUser(userDto);
		comment.setCreatedAt(LocalDateTime.now());

		Comment newComment = commentRepository.save(comment);
		post.getComments().add(newComment);
		postRepository.save(post);

		UserModel postUser = new UserModel();
		postUser.setId(post.getUser().getId());
		postUser.setEmail(post.getUser().getEmail());
		postUser.setName(post.getUser().getName());
		postUser.setUsername(post.getUser().getUsername());
		postUser.setImage(post.getUser().getImage());

//		notificationService.sendNotification(user, postUser, "commented on your post", post);

		return newComment;
	}


	@Override
	public void deleteComment(Integer commentId, Integer currentUserId) throws PostException, UserException {

		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new PostException("Comment not found"));
		if (!comment.getUser().getId().equals(currentUserId)) {
			throw new UserException("You do not have permission to delete this comment.");
		}

		Post post = postService.findPostById(comment.getPost().getId());
		post.getComments().remove(comment);
		postRepository.save(post);
		commentRepository.deleteById(commentId);
	}

	@Override
	public List<Comment> getCommentsByPostId(int postId) throws  PostException {
		Post post = postService.findPostById(postId);
		List<Comment> comments = post.getComments();
		if (comments == null || comments.isEmpty()) {
			return Collections.emptyList();
		}
		return comments.stream()
				.sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
				.collect(Collectors.toList());
	}

}
