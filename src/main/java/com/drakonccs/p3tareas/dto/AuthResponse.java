package com.drakonccs.p3tareas.dto;

public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getJwt() {
        return token;
    }

    public void setJwt(String token) {
        this.token = token;
    }
}
