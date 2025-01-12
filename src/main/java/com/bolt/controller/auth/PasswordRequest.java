package com.bolt.controller.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;

    @NotBlank(message = "Old password is mandatory")
    private String oldPassword;
}
