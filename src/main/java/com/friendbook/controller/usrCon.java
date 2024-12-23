package com.friendbook.controller;

;
import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entity.Post;
import com.friendbook.entity.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.FollowService;
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


@Controller
@RequestMapping("/api/users")
public class usrCon {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;


    @GetMapping("username/{username}")
    public ResponseEntity<UserModel> findByUsernameHandler(@PathVariable("username") String username)
            throws UserException {
        UserModel user = userService.findUserByUsername(username);

        return new ResponseEntity<UserModel>(user, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUserHandler(@RequestParam("q") String query) {
        List<UserModel> users = null;
        try{
            users = userService.searchUser(query);
        } catch (UserException e) {
            new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<UserModel>>(users, HttpStatus.OK);
    }


    @PostMapping("/updateProfile")
    public String updateUserHandler(@ModelAttribute UserModel updatedUser, HttpSession session,Model model)  {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            UserModel userWithUpdates = userService.updateUserDetails(updatedUser, currentUser);
            session.setAttribute("loggedInUser", userWithUpdates);
        }
        catch (UserException e) {
            model.addAttribute("userError",e.getMessage());
        }
        return "redirect:/api/users/profile";

    }

    @PostMapping("/sendRequest/{toUserId}")
    public String sendFollowRequest(@PathVariable Integer toUserId, HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            followService.sendFollowRequest(toUserId, currentUser);
            UserModel targetUser = userService.findUserById(toUserId);
            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();

        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @PostMapping("/removeFollower/{toUserId}")
    public String removeFollower(@PathVariable Integer toUserId, HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            UserModel targetUser = userService.findUserById(toUserId);
            followService.removeFollower(targetUser, currentUser);

            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();

        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @PostMapping("/accept/{requesterId}")
    public String acceptFollowRequest(@PathVariable Integer requesterId,HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            followService.acceptFollowRequest(currentUser, requesterId);
            UserModel targetUser = userService.findUserById(requesterId);
            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @PostMapping("/decline/{requesterId}")
    public String declineFollowRequest(@PathVariable Integer requesterId,HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            UserModel targetUser = userService.findUserById(requesterId);
            followService.declineFollowRequest(currentUser, targetUser);
            boolean isFollowing = followService.isFollowing(currentUser.getId(), targetUser.getId());
            boolean isFollower = followService.isFollower(currentUser.getId(), targetUser.getId());

            List<Post> posts = new ArrayList<>();
            if (isFollowing || isFollower) {
                posts = postService.findPostByUserId(requesterId);
            }
            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @PostMapping("/cancelRequest/{userId}")
    public String cancelRequest(@PathVariable Integer userId,HttpSession session, Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            UserModel targetUser = userService.findUserById(userId);
            followService.cancelFollowRequest(currentUser, targetUser);

            return "redirect:/api/users/viewProfile/"+targetUser.getUsername();
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }

    @GetMapping("/allRequests")
    public String getFollowRequests(HttpSession session,Model model) {
        UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");

            if(currentUser== null){
                return "redirect:/signin";
            }
            Set<UserDto> requests = followService.getFollowRequests(currentUser);
            model.addAttribute("allRequest",requests);

        return "redirect:/api/users/viewProfile/"+currentUser.getUsername();
    }

    @PostMapping("/unfollow/{unfollowUserId}")
    public String unfollowUserHandler(@PathVariable Integer unfollowUserId,
                                      HttpSession session,Model model) {
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            if (currentUser == null) {
                return "redirect:/signin";
            }
            UserModel unfollowUser = userService.findUserById(unfollowUserId);
            followService.unfollowUser(currentUser, unfollowUser);
            return "redirect:/api/users/viewProfile/"+unfollowUser.getUsername();
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            return "viewProfile";
        }
    }
    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> viewFollowers(@PathVariable Integer userId,HttpSession session) {
        try
        {
            Set<UserDto> followers = followService.getFollowers(userId);
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND );
        }
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<?> viewFollowing(@PathVariable Integer userId) {
        try {
            Set<UserDto> following = followService.getFollowing(userId);
            return new ResponseEntity<>(following, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND );
        }
    }

    @GetMapping("/viewProfile/{username}")
    public String viewProfile(@PathVariable String username, Model model,HttpSession session) {
        try{
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            UserModel viewedUser = userService.findUserByUsername(username);
            Map<Integer, Boolean> likedPostsMap = new HashMap<>();
            if(currentUser == null) {
                return "redirect:/signin";
            }
            if(Objects.equals(currentUser.getUsername(), username))
            {
                return "redirect:/api/users/profile";
            }
            if (viewedUser == null ) {
                return "redirect:/home";
            }

            if(currentUser != null){

                boolean isFollowing = followService.isFollowing(currentUser.getId(), viewedUser.getId());
                boolean isFollower = followService.isFollower(currentUser.getId(), viewedUser.getId());
                boolean isInMyFollowRequests = followService.isInMyFollowRequests(currentUser.getId(),viewedUser.getId());
                boolean isRequests = followService.isRequests(currentUser.getId(),viewedUser.getId());

                List<Post> posts = new ArrayList<>();
                if (isFollowing || isFollower) {
                    try {
                        posts = postService.findPostByUserId(viewedUser.getId());
                        model.addAttribute("postCount", postService.getPostCountByUser(viewedUser));
                    } catch (UserException e) {
                        model.addAttribute("postError",e.getMessage());
                        model.addAttribute("postCount",0);
                    }
                }

                Set<UserDto> requests = followService.getFollowRequests(currentUser);
                Map<Integer, Boolean> isFollowingMap = new HashMap<>();
                for (UserDto request : requests) {
                    boolean isFollowingNow = followService.isFollowing(currentUser.getId(), request.getId());
                    isFollowingMap.put(request.getId(), isFollowingNow);
                }

                Map<Integer, Boolean> isInRequestMap= new HashMap<>();
                for (UserDto request : requests) {
                    boolean isFollowingNow = followService.isRequests(currentUser.getId(), request.getId());
                    System.out.println(isFollowingNow);
                    isInRequestMap.put(request.getId(), isFollowingNow);
                }

                model.addAttribute("isInRequestMap", isInRequestMap);
                model.addAttribute("allRequests", requests);
                model.addAttribute("isFollowingMap", isFollowingMap);

                model.addAttribute("user", viewedUser);
                model.addAttribute("currUser", currentUser);
                model.addAttribute("currUserImage",currentUser.getImage());
                model.addAttribute("username",viewedUser.getUsername());
                model.addAttribute("profileImage",viewedUser.getImage());
                model.addAttribute("followersCount", followService.getFollowersCount(viewedUser));
                model.addAttribute("followingCount", followService.getFollowingCount(viewedUser));
                model.addAttribute("posts", posts);
                model.addAttribute("isFollowing", isFollowing);
                model.addAttribute("isFollower", isFollower);
                model.addAttribute("isInFollowRequests", isInMyFollowRequests);
                model.addAttribute("isRequested", isRequests);
                model.addAttribute("followers",followService.getFollowers(viewedUser.getId()));
                model.addAttribute("followings",followService.getFollowing(viewedUser.getId()));
            }
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
        }
        return "viewProfile";
    }

    @GetMapping("/profile")
    public String currentUserProfile( Model model,HttpSession session) {
        try{
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");

            if (currentUser == null) {
                return "redirect:/signin";
            }
            else {
                List<Post> posts = new ArrayList<>();
                try{
                    posts = postService.findPostByUserId(currentUser.getId());
                } catch (UserException e) {
                    model.addAttribute("error",e.getMessage());
                }
                Set<UserDto> requests = followService.getFollowRequests(currentUser);

                Map<Integer, Boolean> isFollowingMap = new HashMap<>();
                for (UserDto request : requests) {
                    boolean isFollowingNow = followService.isFollowing(currentUser.getId(), request.getId());
                    isFollowingMap.put(request.getId(), isFollowingNow);
                }

                Map<Integer, Boolean> isInRequestMap= new HashMap<>();
                for (UserDto request : requests) {
                    boolean isFollowingNow = followService.isRequests(currentUser.getId(), request.getId());
                    System.out.println(isFollowingNow);
                    isInRequestMap.put(request.getId(), isFollowingNow);
                }

                model.addAttribute("isInRequestMap", isInRequestMap);
                model.addAttribute("allRequests", requests);
                model.addAttribute("isFollowingMap", isFollowingMap);
                model.addAttribute("user", currentUser);
                model.addAttribute("post", posts);
                model.addAttribute("followers",followService.getFollowers(currentUser.getId()));
                model.addAttribute("followings",followService.getFollowing(currentUser.getId()));
                return "profile";
            }
        } catch (UserException e) {
            System.out.println(e.getMessage());
            model.addAttribute("error",e.getMessage());
            return "profile";
        }
    }

}
