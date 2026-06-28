package com.kapiki_akapikebula.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//კაროჩე ეს არის ის რასაც API დააბრუნებს register ის მერე და თუ
// user entity ში სხვა ველებსაც დაამატებთ
//აქაც უნდა დაამტო მაშინ UserService.registerUser() მინიჭება.
@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
}
