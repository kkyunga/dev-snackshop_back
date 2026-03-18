package org.back.devsnackshop_back.dto.serververManage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerUpdateRequest {
    private Long userOsId;
    private String label;
    private String ip;
    private String port;
    private String osType;
    private String osVersion;
    private String country;
    private String cloudService;
    private String purpose;
    private String authType;
    private String username;
    private String password;
    private boolean keyFileDelete;
}
