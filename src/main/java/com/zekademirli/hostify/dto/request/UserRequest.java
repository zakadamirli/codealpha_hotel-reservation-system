package com.zekademirli.hostify.dto.request;

import com.zekademirli.hostify.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private Role role;
    private Boolean isActive;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}