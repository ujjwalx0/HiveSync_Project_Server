package com.hivesync.Model;

public interface UserLoginProjection {
    String getUsername();
    String getEmail();
    String getPassword();
    boolean isAccountNonLocked();
}
