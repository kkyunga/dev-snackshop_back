package org.back.devsnackshop_back.dto.middlewareManage;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class InstallRequest {
    private Long userOsInstanceId;
    private String os;
    private String ip;
    private Long port = 22L;
    private String username;
    private String password;
    private MultipartFile keyFile;
    private String installPath;
    private List<String> middlewares;
    private String mwVersion;
}
