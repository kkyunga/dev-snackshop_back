package org.back.devsnackshop_back.dto.middlewareManage;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class InstallRequest {
    private Long userOsInstanceId;
    private String installPath;
    private List<String> middlewares;
    private List<String> mwVersion;
}
