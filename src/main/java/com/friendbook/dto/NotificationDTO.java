package com.friendbook.dto;

import com.friendbook.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor

public class NotificationDTO {
    private Integer id;
    private String fromUsername;
    private String toUsername;
    private String message;
    private String userImage;
    private LocalDateTime createdAt;
    private Post post;
}