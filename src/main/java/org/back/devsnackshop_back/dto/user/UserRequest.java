package org.back.devsnackshop_back.dto.user;

import lombok.Data;

@Data
public class UserRequest {

    private String email;
    private String name;
    private String password;
    private String newPassword;
    private String phone;
}
