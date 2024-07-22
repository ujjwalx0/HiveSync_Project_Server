package com.hivesync.Response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private String token; 

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.token = null;
    }
}
