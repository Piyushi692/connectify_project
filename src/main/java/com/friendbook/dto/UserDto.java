package com.friendbook.dto;

import com.friendbook.entity.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {
	private Integer id;
	private String username;
	private String email;
	private String name;
	private String image;
	private String mobile;
	private String bio;

    public UserDto(Integer id, String username) {
		this.id = id;
		this.username = username;
    }

    public UserDto(Integer id, String username, String image,String name) {
		this.id = id;
		this.username=username;
		this.image = image;
		this.name = name;
    }

    public UserModel toUserModel() {
		UserModel user = new UserModel();
		user.setId(this.id);
		user.setEmail(this.email);
		user.setName(this.name);
		user.setUsername(this.username);
		user.setImage(this.image);
		user.setMobile(this.mobile);
		user.setBio(this.bio);
		return user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserDto userDto = (UserDto) o;
		return Objects.equals(id, userDto.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);	}
}
