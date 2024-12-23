package com.friendbook.controller;

import com.friendbook.Exception.UserException;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;
import com.friendbook.service.FollowService;
import com.friendbook.service.PostService;
import com.friendbook.dto.UserDto;
import com.friendbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.*;
@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    @Autowired
    UserService userService;

    @GetMapping
    public String homePagePost(HttpSession session, Model model) {
        UserModel loggedInUser = (UserModel) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {

            try {
                Set<UserDto> following = followService.getFollowing(loggedInUser.getId());

                Set<Integer> userIds = new HashSet<>();
                Set<Post> allPost = new HashSet<>();
                Map<Integer, Boolean> likedPostsMap = new HashMap<>();
                try{
                    List<Post> posts = postService.findPostByUserId(loggedInUser.getId());
                    allPost.addAll(posts);
                    for (UserDto followings : following) {
                        if (!followings.getId().equals(loggedInUser.getId())) {
                            userIds.add(followings.getId());
                        }
                    }
                } catch (UserException e) {
                    model.addAttribute("exception",e.getMessage());
                }

                for (Integer userId : userIds) {
                    try {
                        List<Post> posts = postService.findPostByUserId(userId);
                        allPost.addAll(posts);
                    } catch (UserException e) {
                        System.out.println(e.getMessage());
                    }
                }

                for (Post post : allPost) {
                    boolean likedByCurrentUser = post.getLikedByUser().stream()
                            .anyMatch(user -> user.getId().equals(loggedInUser.getId()));
                    likedPostsMap.put(post.getId(), likedByCurrentUser);
                }

                 Set<UserDto> requests = followService.getFollowRequests(loggedInUser);
                Map<Integer, Boolean> isFollowingMap = new HashMap<>();
                for (UserDto request : requests) {
                    boolean isFollowing = followService.isFollowing(loggedInUser.getId(), request.getId());
                    isFollowingMap.put(request.getId(), isFollowing);
                }

                Map<Integer, Boolean> isInRequestMap= new HashMap<>();
                for (UserDto request : requests) {
                    boolean isFollowingNow = followService.isRequests(loggedInUser.getId(), request.getId());
                    System.out.println(isFollowingNow);
                    isInRequestMap.put(request.getId(), isFollowingNow);
                }

                model.addAttribute("isInRequestMap", isInRequestMap);

                model.addAttribute("allRequests", requests);
                model.addAttribute("isFollowingMap", isFollowingMap);
                model.addAttribute("user", loggedInUser);
                model.addAttribute("profileImage",loggedInUser.getImage());
                model.addAttribute("allPosts", allPost);
                model.addAttribute("likedPostsMap", likedPostsMap);
                return "home";

            } catch (UserException e) {
                model.addAttribute("error", e.getMessage());
                return "home";
            }

        }
        return "redirect:/signin";
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchUserHandler(@RequestParam("q") String query,HttpSession session) {
        List<UserModel> users = null;
        try{
            users = userService.searchUser(query);
        } catch (UserException e) {
            new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<UserModel>>(users, HttpStatus.OK);
    }

}
