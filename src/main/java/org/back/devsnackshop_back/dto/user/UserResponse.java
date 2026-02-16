package org.back.devsnackshop_back.dto.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String email;
    private String name;
    private String phoneNumber;
}
