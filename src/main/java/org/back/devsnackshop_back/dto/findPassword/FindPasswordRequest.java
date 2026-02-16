package org.back.devsnackshop_back.dto.findPassword;

import lombok.Data;

@Data
public class FindPasswordRequest {
    private String email;
    private String name;
    private String phone;

}
