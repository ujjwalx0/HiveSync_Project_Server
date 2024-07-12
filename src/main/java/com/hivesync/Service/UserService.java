package com.hivesync.Service;

import com.hivesync.Model.User;
import com.hivesync.Model.LoginRequestDto;
import com.hivesync.Repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(@Lazy UserRepository userRepository, @Lazy BCryptPasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void save(User user) {
        logger.debug("Saving user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedTimestamp(LocalDateTime.now());
        userRepository.save(user);
        logger.debug("User saved successfully: {}", user.getUsername());
    }

    public User findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        logger.debug("Finding user by username or email: {}", usernameOrEmail);
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void updateLastLogin(String username, String ipAddress) {
        logger.debug("Updating last login for user: {}", username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        user.setLastIpAddress(ipAddress);
        userRepository.save(user);
        logger.debug("Last login updated for user: {}", username);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (user == null) {
            logger.error("User not found: {}", usernameOrEmail);
            throw new UsernameNotFoundException("User not found");
        }

        logger.debug("User loaded: {}", usernameOrEmail);
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles("USER")
            .accountLocked(!user.isAccountNonLocked())
            .build();
    }

    public void authenticate(LoginRequestDto loginRequest) throws Exception {
        try {
            logger.debug("Authenticating user: {}", loginRequest.getUsernameOrEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
            logger.debug("Authentication successful for user: {}", loginRequest.getUsernameOrEmail());
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsernameOrEmail());
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
