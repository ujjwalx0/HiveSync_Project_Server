package com.hivesync.Service;

import com.hivesync.Model.User;
import com.hivesync.Model.UserLoginDto;
import com.hivesync.Model.UserRegistrationDto;
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
    public void registerUser(UserRegistrationDto registrationRequest) {
        if (userRepository.existsByUsernameOrEmail(registrationRequest.getUsername(), registrationRequest.getEmail())) {
            throw new RuntimeException("Username or email already exists");
        }

        logger.debug("Saving user: {}", registrationRequest.getUsername());
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setEmail(registrationRequest.getEmail());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setCreatedTimestamp(LocalDateTime.now());

        userRepository.save(user);
        logger.debug("User registered successfully: {}", registrationRequest.getUsername());
    }
    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameOrEmail(username, null);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByUsernameOrEmail(null, email);
    }
    @Transactional
    public void updateLastLogin(String username, String ipAddress) {
        logger.debug("Updating last login for user: {}", username);
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        user.setLastIpAddress(ipAddress);
        userRepository.save(user);
        logger.debug("Last login updated for user: {}", username);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .accountLocked(!user.isAccountNonLocked())
                .accountExpired(!user.isAccountNonExpired())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .build();
    }

    public void authenticate(UserLoginDto loginRequest) throws Exception {
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
