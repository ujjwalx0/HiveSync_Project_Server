package com.hivesync.Model;

import lombok.AllArgsConstructor;


import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private final String token;
    private final String message;
    
	public JwtResponse(String token) {
        this.token = token;
        this.message =null;
    }
}
