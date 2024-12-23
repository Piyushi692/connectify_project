package com.friendbook.controller;


import com.friendbook.Exception.CommentException;
import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.entity.Comment;
import com.friendbook.entity.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.CommentService;
import com.friendbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/comments")

public class CommentController {

	@Autowired
	private CommentService commentService;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/{postId}/comment")
	public ResponseEntity<?> addCommentToPost(
			@PathVariable Integer postId,
			@RequestBody Comment comment,
			HttpSession session) {
		try {
			UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
			Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
			if (user.isPresent()){
				commentService.createComment(comment, postId, user.get());
				return new ResponseEntity<>("Comment added successfully.", HttpStatus.CREATED);
			}else {
				return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
			}
		} catch (PostException e) {
			return  new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}

    }

	@GetMapping("getCommentsByPostId/{postId}")
	public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable int postId) {
		List<Comment> comments = null;
		try {
			comments = commentService.getCommentsByPostId(postId);
			return new ResponseEntity<>(comments, HttpStatus.OK);
		} catch (PostException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}


}
