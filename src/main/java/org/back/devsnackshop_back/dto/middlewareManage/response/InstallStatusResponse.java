package org.back.devsnackshop_back.dto.middlewareManage.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstallStatusResponse {
    private String middlewareName;
    private String version;
    private String status;
    private LocalDateTime installedAt;
}
