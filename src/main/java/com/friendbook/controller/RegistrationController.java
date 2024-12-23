package com.friendbook.controller;

import com.friendbook.Exception.UserException;

import com.friendbook.entity.UserModel;
import com.friendbook.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/signup")
public class RegistrationController {

    private UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserModel registrationDto(){
        return new UserModel();
    }

    @GetMapping()
    public String registerUser() {
        try {
            return "registration";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "errorPage";
        }
    }

    @PostMapping()
    public String registerUserAccount(@ModelAttribute("user") @RequestBody UserModel user, Model model){
       try {
           UserModel createdUser = userService.registerUser(user);
           if(createdUser !=null) {
               model.addAttribute("success", "Account Created Successfully!");
              // return "registration";
           }
       }catch (UserException e) {
           System.out.println(e.getMessage());
           model.addAttribute("error", e.getMessage());
           return "registration";
       }
        return "registration";
    }
}
