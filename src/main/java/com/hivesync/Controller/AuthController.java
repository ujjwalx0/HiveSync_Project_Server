package com.hivesync.Controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hivesync.Model.UserLoginDto;
import com.hivesync.Model.UserRegistrationDto;
import com.hivesync.Response.ApiResponse;
import com.hivesync.Security.JwtTokenUtil;
import com.hivesync.Service.UserService;


@RestController
@RequestMapping("/hivesync")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    @Lazy
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Lazy
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        logger.debug("Attempting to register user: {}", userRegistrationDto.getUsername());

        if (userService.usernameExists(userRegistrationDto.getUsername())) {
            logger.debug("Username already exists: {}", userRegistrationDto.getUsername());
            return ResponseEntity.status(409).body(new ApiResponse(false, "Username already exists"));
        }

        if (userService.emailExists(userRegistrationDto.getEmail())) {
            logger.debug("Email already exists: {}", userRegistrationDto.getEmail());
            return ResponseEntity.status(409).body(new ApiResponse(false, "Email already exists"));
        }

        userService.registerUser(userRegistrationDto);
        logger.debug("User registered successfully: {}", userRegistrationDto.getUsername());
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> createAuthenticationToken(@RequestBody UserLoginDto userLoginDto) {
        logger.debug("Login request for user: {}", userLoginDto.getUsernameOrEmail());

        try {
            userService.authenticate(userLoginDto);

            final UserDetails userDetails = userService.loadUserByUsername(userLoginDto.getUsernameOrEmail());
            final String token = jwtTokenUtil.generateToken(userDetails.getUsername());

            logger.debug("Login successful for user: {}", userLoginDto.getUsernameOrEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Login successful", token));
        } catch (Exception e) {
            if (e.getMessage().equals("INVALID_CREDENTIALS")) {
                logger.debug("Invalid credentials for user: {}", userLoginDto.getUsernameOrEmail());
                return ResponseEntity.status(401).body(new ApiResponse(false, "Invalid credentials"));
            }
            logger.error("Login error for user: {}", userLoginDto.getUsernameOrEmail(), e);
            return ResponseEntity.status(500).body(new ApiResponse(false, "An error occurred during login"));
        }
    }
}
