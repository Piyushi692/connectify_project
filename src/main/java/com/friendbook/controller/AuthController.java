package com.friendbook.controller;

import com.friendbook.repository.UserRepository;
import com.friendbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logout")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@GetMapping
	public String  logout(Model model, HttpSession session) {
		session.invalidate();
		model.addAttribute("error", "Successfully logged out");
		return "redirect:/signin";
	}

//	@PostMapping("/signup")
//	public ResponseEntity<UserModel> registerUserHandler(@RequestBody UserModel user) throws UserException {
//		UserModel createdUser = userService.registerUser(user);
//
//		return new ResponseEntity<UserModel>(createdUser, HttpStatus.CREATED);
//	}

//	@PostMapping("/signin")
//	public ResponseEntity<UserModel> signinHandler(Authentication auth) throws UserException {
//		Optional<UserModel> optionalUser = userRepository.findByEmail(auth.);
//		if (optionalUser.isPresent()) {
//			return new ResponseEntity<UserModel>(optionalUser.get(), HttpStatus.ACCEPTED);
//		}
//		throw new UserException("invalid username or password");
//	}


}
