package com.friendbook.service.impl;

import com.friendbook.Exception.UserException;
import com.friendbook.dto.UserDto;
import com.friendbook.entity.Notification;
import com.friendbook.entity.UserModel;
import com.friendbook.service.FollowService;
import com.friendbook.repository.NotificationRepository;
import com.friendbook.repository.UserRepository;

import com.friendbook.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void sendFollowRequest(Integer toUserId, UserModel fromUser) throws UserException {

        UserModel toUser = userService.findUserById(toUserId);
        UserDto fromUserDto = new UserDto(fromUser.getId(), fromUser.getUsername(), fromUser.getEmail(), fromUser.getName(), fromUser.getImage(),fromUser.getMobile(),fromUser.getBio());
        toUser.getFollowRequests().add(fromUserDto);
        Notification notification = new Notification();
        notification.setFromUser(fromUser);
        notification.setToUser(toUser);
        notification.setMessage(fromUser.getUsername() + " sent you a follow request.");
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        toUser.getNotifications().add(notification);

        userRepository.save(toUser);
    }

    @Override
    public void acceptFollowRequest(UserModel user, Integer requesterId) throws UserException {

            UserModel requester = userService.findUserById(requesterId);

            UserDto fromUserDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getName(), user.getImage(),user.getMobile(),user.getBio());
            UserDto toUserDto = new UserDto(requester.getId(), requester.getUsername(), requester.getEmail(), requester.getName(), requester.getImage(),requester.getMobile(),requester.getBio());

            if (user.getFollowRequests().stream().anyMatch(dto -> dto.getId().equals(requester.getId()))) {

                user.getFollowRequests().removeIf(dto -> dto.getId().equals(toUserDto.getId()));
                user.getFollower().add(toUserDto);
                requester.getFollowing().add(fromUserDto);

                userRepository.save(user);

                Notification notification = new Notification();
                notification.setFromUser(user);
                notification.setToUser(requester);
                notification.setMessage(user.getUsername() + " accepted your follow request.");
                notification.setCreatedAt(LocalDateTime.now());

                notificationRepository.save(notification);
                requester.getNotifications().add(notification);
                userRepository.save(requester);
            }
    }

    @Override
    public void declineFollowRequest(UserModel user, UserModel requester) throws UserException {

        UserDto toUserDto = new UserDto(requester.getId(), requester.getUsername(), requester.getEmail(), requester.getName(), requester.getImage(),requester.getMobile(),requester.getBio());
        if (user.getFollowRequests().removeIf(dto -> dto.getId().equals(toUserDto.getId()))) {
            userRepository.save(user);
        } else {
            throw new UserException("Follow request not found");
        }
    }

    @Override
    public void cancelFollowRequest(UserModel user, UserModel requester) throws UserException {
        UserDto toUserDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getName(), user.getImage(),user.getMobile(),user.getBio());

        if (requester.getFollowRequests().removeIf(dto -> dto.getId().equals(toUserDto.getId()))) {
            userRepository.save(requester);
        } else {
            throw new UserException("Follow request not found");
        }
    }

    @Override
    public void removeFollower(UserModel targetUser, UserModel currentUser)  {

            UserDto currentUserDto = new UserDto(currentUser.getId(), currentUser.getUsername(), currentUser.getEmail(), currentUser.getName(), currentUser.getImage(), currentUser.getMobile(), currentUser.getBio());
            UserDto targetUserDto = new UserDto(targetUser.getId(), targetUser.getUsername(), targetUser.getEmail(), targetUser.getName(), targetUser.getImage(), targetUser.getMobile(), targetUser.getBio());

            currentUser.getFollower().removeIf(dto -> dto.getId().equals(targetUserDto.getId()));

            targetUser.getFollowing().removeIf(dto -> dto.getId().equals(currentUserDto.getId()));

            userRepository.save(currentUser);
            userRepository.save(targetUser);

    }

    @Override
    @Transactional
    public void unfollowUser(UserModel reqUser, UserModel unfollowUser) throws UserException {

        UserDto currUserDto = new UserDto(reqUser.getId(), reqUser.getUsername(), reqUser.getEmail(), reqUser.getName(), reqUser.getImage(), reqUser.getMobile(), reqUser.getBio());
        UserDto otherUserDto = new UserDto(unfollowUser.getId(), unfollowUser.getUsername(), unfollowUser.getEmail(), unfollowUser.getName(), unfollowUser.getImage(), unfollowUser.getMobile(), unfollowUser.getBio());

        if (unfollowUser.getFollower().contains(currUserDto)) {
            unfollowUser.getFollower().remove(currUserDto);
        } else {
            throw new UserException("User is not in followers list");
        }

        if (reqUser.getFollowing().contains(otherUserDto)) {
            reqUser.getFollowing().remove(otherUserDto);
        } else {
            throw new UserException("You are not following this user");
        }


        userRepository.save(reqUser);
        userRepository.save(unfollowUser);
    }

    @Override
    public Object getFollowersCount(UserModel viewedUser) {
        return viewedUser.getFollower().size();
    }

    @Override
    public Object getFollowingCount(UserModel viewedUser) {
        return viewedUser.getFollowing().size();
    }

    @Override
    public boolean isFollowing(Integer currentUserId, Integer viewedUserId) {
        UserModel currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null || currentUser.getFollowing() == null) {
            return false;
        }

        return currentUser.getFollowing().stream()
                .anyMatch(userDto -> userDto.getId().equals(viewedUserId));
    }

    @Override
    public boolean isFollower(Integer currentUserId, Integer viewedUserId) {
        UserModel currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null || currentUser.getFollowing() == null) {
            return false;
        }

        return currentUser.getFollower().stream()
                .anyMatch(userDto -> userDto.getId().equals(viewedUserId));
    }

    @Override
    public boolean isRequests(Integer currentUserId, Integer targetUserId) {

        UserModel targetUser = userRepository.findById(targetUserId).orElse(null);
        if (targetUser == null || targetUser.getFollowRequests() == null) {
            return false;
        }
        return targetUser.getFollowRequests().stream()
                .anyMatch(request -> request.getId().equals(currentUserId));
    }


    @Override
    public boolean isInMyFollowRequests(Integer currentUserId, Integer targetUserId) {
        UserModel currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser == null || currentUser.getFollowRequests() == null) {
            return false;
        }
        return currentUser.getFollowRequests().stream()
                .anyMatch(request -> request.getId().equals(targetUserId));
    }

    @Override
    public Set<UserDto> getFollowRequests(UserModel user)  {
        return user.getFollowRequests();
    }

    @Override
    public Set<UserDto> getFollowers(Integer userId) throws UserException {
        Optional<UserModel> user = userRepository.findById(userId);
        if(user.isEmpty())
        {
            throw new UserException("User not found");
        }
        return user.get().getFollower();
    }

    @Override
    public Set<UserDto> getFollowing(Integer userId) throws UserException {
        Optional<UserModel> user = userRepository.findById(userId);
        if(user.isEmpty())
        {
            throw new UserException("User not found");
        }
        return user.get().getFollowing();
    }
}
