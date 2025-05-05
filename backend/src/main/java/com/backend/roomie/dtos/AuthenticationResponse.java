package com.backend.roomie.dtos;

import com.backend.roomie.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}