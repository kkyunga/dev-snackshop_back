package org.back.devsnackshop_back.dto.serververManage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServerCreateRequest {

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
    private List<SoftwareItem> softwareToInstall;

    @Getter
    @Setter
    public static class SoftwareItem {
        private String name;
        private String path;
    }
}
