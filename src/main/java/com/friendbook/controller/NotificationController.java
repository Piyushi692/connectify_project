package com.friendbook.controller;


import com.friendbook.Exception.UserException;
import com.friendbook.dto.NotificationDTO;
import com.friendbook.entity.UserModel;
import com.friendbook.service.UserService;
import com.friendbook.service.impl.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getNotifications(HttpSession session){
        try {
            UserModel currentUser = (UserModel) session.getAttribute("loggedInUser");
            List<NotificationDTO> notifications = notificationService.getNotifications(currentUser.getId());
            return ResponseEntity.ok(notifications);
        } catch (UserException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
