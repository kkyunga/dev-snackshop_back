package org.back.devsnackshop_back.dto.updatePassword;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String email;
    private String newPassword;
}
