package com.friendbook.repository;

import com.friendbook.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

	 Optional<UserModel> findByEmail(String email);

	 Optional<UserModel> findByUsername(String username);

	@Query("SELECT DISTINCT u FROM UserModel u WHERE u.username LIKE %:query% OR u.email LIKE %:query%")
	 List<UserModel> findByQuery(@Param("query") String query);

}
