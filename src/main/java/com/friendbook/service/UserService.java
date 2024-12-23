package com.friendbook.service;



import com.friendbook.Exception.UserException;
import com.friendbook.entity.UserModel;

import java.util.List;

public interface UserService {

	 UserModel registerUser(UserModel user) throws UserException;

	 UserModel findUserById(Integer userId) throws UserException;

	 UserModel findUserByUsername(String username) throws UserException;

	 List<UserModel> searchUser(String query) throws UserException;

	 UserModel updateUserDetails(UserModel updatedUser, UserModel existingUser) throws UserException;
}
