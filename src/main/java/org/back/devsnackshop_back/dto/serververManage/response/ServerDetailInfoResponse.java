package org.back.devsnackshop_back.dto.serververManage.response;

import lombok.Data;
import org.back.devsnackshop_back.dto.serververManage.ServerCreateRequest;

import java.util.List;
@Data
public class ServerDetailInfoResponse {
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
    private List<ServerCreateRequest.SoftwareItem> softwareToInstall;
    private String cpuInfo;

}
