package com.hivesync.Controller;

import com.hivesync.Model.Role;
import com.hivesync.Service.UserService;
import com.hivesync.Response.ApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/update-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateRole(
            @RequestParam String usernameOrEmail,
            @RequestParam Role newRole,
            @RequestParam String updatedBy) {

        logger.debug("Admin {} is updating role for user {}", updatedBy, usernameOrEmail);

        ApiResponse response = userService.updateUserRole(usernameOrEmail, newRole, updatedBy);

        return ResponseEntity.ok(response);
    }

}
