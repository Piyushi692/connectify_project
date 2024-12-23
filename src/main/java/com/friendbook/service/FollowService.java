package com.friendbook.service;

import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entity.UserModel;


import java.util.Set;

public interface FollowService {

    boolean isInMyFollowRequests(Integer currentUserId, Integer targetUserId);

    Set<UserDto> getFollowRequests(UserModel userModel) ;

    Set<UserDto> getFollowers(Integer userId) throws UserException;

    Set<UserDto> getFollowing(Integer userId) throws UserException;

    void sendFollowRequest(Integer toUserId, UserModel currUser) throws UserException;

    void acceptFollowRequest(UserModel currUser, Integer requesterId) throws UserException;

    void declineFollowRequest(UserModel user, UserModel requesterUser) throws UserException;

    void unfollowUser(UserModel reqUser, UserModel unfollowUser) throws UserException;

    Object getFollowersCount(UserModel viewedUser);

    Object getFollowingCount(UserModel viewedUser);

    boolean isFollowing(Integer currentUSerId, Integer viewedUserId);

    boolean isFollower(Integer currentUSerId, Integer viewedUserId);

    boolean isRequests(Integer currentUserId, Integer viewedUserId);

    void cancelFollowRequest(UserModel currUser, UserModel sendToUser) throws UserException;

    void removeFollower(UserModel toUser, UserModel currUser)throws UserException;
}
