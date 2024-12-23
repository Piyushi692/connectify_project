package com.friendbook.service.impl;

import com.friendbook.Exception.UserException;
import com.friendbook.entity.Comment;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;
import com.friendbook.repository.CommentRepository;
import com.friendbook.repository.PostRepository;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.CommentService;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;
import com.friendbook.dto.UserDto;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
	private static Set<String> userName = new HashSet<>();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	PostRepository postRepository;
	@Autowired
	PostService postService;

	@Override
	public UserModel registerUser(UserModel user) throws UserException {

		if (user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
			throw new UserException("Email, Username and Password are required");
		}
		Optional<UserModel> isEmailExist = userRepository.findByEmail(user.getEmail());
		if (isEmailExist.isPresent()) {
			throw new UserException("Email Already Exist.");
		}
		user.setUsername(getUsername(user.getName()));
		Optional<UserModel> isUsernameExist = userRepository.findByUsername(user.getUsername());
		if (isUsernameExist.isPresent()) {
			throw new UserException("Username Already Taken");
		}

		//String encodedPassword = passwordEncoder.encode(user.getPassword());

		UserModel newUser = new UserModel();

		newUser.setEmail(user.getEmail());
		//newUser.setPassword(encodedPassword);
		newUser.setPassword(user.getPassword());
		newUser.setUsername(user.getUsername());
		newUser.setName(user.getName());

		return userRepository.save(newUser);
	}

	@Transactional
	@Override
	public UserModel findUserById(Integer userId) throws UserException {
		Optional<UserModel> optionalUser = userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			return optionalUser.get();
		}
		throw new UserException("user not exist with id: " + userId);
	}

	@Override
	public UserModel findUserByUsername(String username) throws UserException {
		Optional<UserModel> optionalUser = userRepository.findByUsername(username);

		if (optionalUser.isPresent()) {
			UserModel user = optionalUser.get();
			return user;
		}
		throw new UserException("user not exist with username " + username);
	}

	@Override
	public List<UserModel> searchUser(String query) throws UserException {
		List<UserModel> users = userRepository.findByQuery(query);
		if (users.isEmpty()) {
			throw new UserException("user not exist");
		}
		return users;
	}

	@Override
	public UserModel updateUserDetails(UserModel updatedUser, UserModel existingUser) throws UserException {
        if (updatedUser.getBio() != null) {
            existingUser.setBio(updatedUser.getBio());
        }

        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }

        if (updatedUser.getMobile() != null) {
            existingUser.setMobile(updatedUser.getMobile());
        }

        if (updatedUser.getImage() != null) {
            existingUser.setImage(updatedUser.getImage());
        }

        userRepository.save(existingUser);
        List<Post> posts = postService.findPostByUserId(existingUser.getId());
        for (Post post : posts) {
			post.setUser(new UserDto(existingUser.getId(),existingUser.getUsername(),existingUser.getImage(),existingUser.getName()));
			postRepository.save(post);
        }

		List<Post> likedPost = postService.findAllPostsLikedByUser(existingUser);
		for (Post post : likedPost) {
			Set<UserDto> likedByUsers = post.getLikedByUser();
			for (UserDto userDto : likedByUsers) {
				if (userDto.getId().equals(existingUser.getId())) {
					userDto.setEmail(existingUser.getEmail());
					userDto.setName(existingUser.getName());
					userDto.setImage(existingUser.getImage());
				}
			}
			postRepository.save(post);
		}

		List<Comment> comments = commentRepository.findAllCommentsByUserId(existingUser.getId());
		for (Comment comment : comments) {
			UserDto userDto = comment.getUser();
			if (userDto.getId().equals(existingUser.getId())) {
				userDto.setEmail(existingUser.getEmail());
				userDto.setName(existingUser.getName());
				userDto.setImage(existingUser.getImage());
				commentRepository.save(comment);
			}
		}

		return existingUser;

    }

	private String getUsername(String name)
	{
		String[] nameParts = name.split(" ");
		String firstName = nameParts[0];
		Random random = new Random();
		int min = 100;
		int max = 99999;
		long randomNumber = random.nextInt(max - min + 1) + min;
		while (userName.contains(firstName + randomNumber))
		{
			randomNumber = random.nextInt(max - min + 1) + min;
		}
		userName.add(firstName + randomNumber);
		return firstName+randomNumber;
	}

}
