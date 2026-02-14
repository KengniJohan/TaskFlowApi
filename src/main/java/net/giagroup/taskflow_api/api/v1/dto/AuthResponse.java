package net.giagroup.taskflow_api.api.v1.dto;

public record AuthResponse(String token, String tokenType, long expiresIn) {}
