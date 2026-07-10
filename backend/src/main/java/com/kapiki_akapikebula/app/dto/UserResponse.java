package com.kapiki_akapikebula.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
}
