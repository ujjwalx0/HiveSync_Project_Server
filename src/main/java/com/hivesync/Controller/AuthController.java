package com.hivesync.Controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hivesync.Model.JwtResponse;
import com.hivesync.Model.LoginRequestDto;
import com.hivesync.Model.User;
import com.hivesync.Security.JwtTokenUtil;
import com.hivesync.Service.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Lazy
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        logger.debug("Attempting to register user: {}", user.getUsername());

        if (userService.usernameExists(user.getUsername())) {
            logger.debug("Username already exists: {}", user.getUsername());
            return "Username already exists";
        }

        if (userService.emailExists(user.getEmail())) {
            logger.debug("Email already exists: {}", user.getEmail());
            return "Email already exists";
        }

        userService.save(user);
        logger.debug("User registered successfully: {}", user.getUsername());
        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDto loginRequest) {
        logger.debug("Login request for user: {}", loginRequest.getUsernameOrEmail());
        try {
            userService.authenticate(loginRequest);

            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsernameOrEmail());
            final String token = jwtTokenUtil.generateToken(userDetails.getUsername());

            logger.debug("Login successful for user: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(new JwtResponse(token, "Login successful"));
        } catch (Exception e) {
            if (e.getMessage().equals("INVALID_CREDENTIALS")) {
                logger.debug("Invalid credentials for user: {}", loginRequest.getUsernameOrEmail());
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            logger.error("Login error for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ResponseEntity.status(500).body("An error occurred during login");
        }
    }
}
