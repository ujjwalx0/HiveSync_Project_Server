package com.hivesync.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class UserLoginDto {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
