package com.friendbook.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.friendbook.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"follower", "following", "followRequests", "notifications"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class UserModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;

	private String username;
	private String name;
	private String email;
	private String mobile;
	private String bio;
	private String image="https://images.app.goo.gl/Tz8mx3QDqc6rm55w6";
	private String password;

	@Embedded
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UserDto> follower = new HashSet<UserDto>();

	@Embedded
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UserDto> following = new HashSet<UserDto>();

	@Embedded
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UserDto> followRequests = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<Notification> notifications = new ArrayList<>();
}
