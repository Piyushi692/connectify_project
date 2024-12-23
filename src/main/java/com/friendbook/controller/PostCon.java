package com.friendbook.controller;

import com.friendbook.Exception.PostException;
import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.PostService;
import com.friendbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/posts")
public class PostCon {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createPostHandler(@RequestBody Post post, @RequestHeader("email") String email, Model model)  {
        Post createdPost = null;
        try {
            Optional<UserModel> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                createdPost = postService.createPost(post, user.get());
            }
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PostMapping("/delete/{postId}")
    public ResponseEntity<String> deletePostHandler(@PathVariable Integer postId,HttpSession session)  {
        UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
        try {
            postService.deletePost(postId, currentUser);
            return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
        } catch (PostException | UserException e  ) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> findPostByUserId(@PathVariable Integer userId)  {
        try {
            List<Post> posts = postService.findPostByUserId(userId);
            Map<Integer, Boolean> likedPostsMap = new HashMap<>();

            for (Post post : posts) {
                boolean likedByCurrentUser = post.getLikedByUser().stream()
                        .anyMatch(user -> user.getId().equals(userId));
                likedPostsMap.put(post.getId(), likedByCurrentUser);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("likedPostsMap", likedPostsMap);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/following/{userIds}")
    public ResponseEntity<?> findAllPostByUserIdsHandler(@PathVariable List<Integer> userIds)  {
        try {
            List<Post> posts = postService.findAllPostByUserIds(userIds);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (PostException e) {
            return  new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetails(@PathVariable int postId) {
           try
           {
               Post post = postService.findPostById(postId);
               return new ResponseEntity<>(post,HttpStatus.FOUND);

           } catch (PostException e) {
               return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
           }
    }


    @GetMapping("/{postId}/likedUser")
    public ResponseEntity<?> getTotalLikes(@PathVariable Integer postId) {
        try {
            Post post = postService.findPostById(postId);
            Set<UserDto> likedUsers = post.getLikedByUser();

            List<UserDto> users = likedUsers.stream()
                    .map(user -> new UserDto(user.getId(), user.getUsername(), user.getImage(),user.getName()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } catch (PostException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePostHandler(@PathVariable Integer postId, HttpSession session)  {
       try{
           UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
           Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
           if (user.isPresent()) {
               Post likedPost = postService.likePost(postId, loggedInUser);
               Map<String, Object> response = new HashMap<>();
               response.put("liked", true);
               response.put("posts", likedPost);
               response.put("likeCount", likedPost.getLikedByUser().size());
               return ResponseEntity.ok(response);
           }else {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
           }
       } catch (PostException | UserException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
       }

    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePostHandler(@PathVariable Integer postId, HttpSession session)  {
        try{
            UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
            Optional<UserModel> user = userRepository.findByEmail(loggedInUser.getEmail());
            if (user.isPresent()) {
                Post unlikedPost = postService.unlikePost(postId, user.get());
                Map<String, Object> response = new HashMap<>();
                response.put("liked", false);
                response.put("post", unlikedPost);
                response.put("likeCount", unlikedPost.getLikedByUser().size());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        }
        catch (PostException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
