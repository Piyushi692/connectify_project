package com.friendbook.entity;


import com.friendbook.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Post")
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "user_id")),
			@AttributeOverride(name = "email", column = @Column(name = "user_email")) })
	private UserDto user;

	@Column
	private String caption;

	@Column(nullable = false)
	private String imagePost;

	private LocalDateTime createdAt;

	@OneToMany
	private List<Comment> comments = new ArrayList<>();

	@Embedded
	@ElementCollection
	@JoinTable(name = "likedByUsers", joinColumns = @JoinColumn(name = "user_id"))
	private Set<UserDto> likedByUser = new HashSet<UserDto>();

}
