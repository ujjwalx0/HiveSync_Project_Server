package com.hivesync.Repository;

import com.hivesync.Model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?2")
    User findByUsernameOrEmail(String username, String email);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);
}
