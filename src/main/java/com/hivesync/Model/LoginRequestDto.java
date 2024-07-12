package com.hivesync.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String usernameOrEmail;
    private String password;
}
