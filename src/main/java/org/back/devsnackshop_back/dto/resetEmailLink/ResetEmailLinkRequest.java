package org.back.devsnackshop_back.dto.resetEmailLink;

import lombok.Data;

@Data
public class ResetEmailLinkRequest {
    private String name;
    private String email;

}
