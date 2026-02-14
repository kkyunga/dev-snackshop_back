package org.back.devsnackshop_back.dto.middlewareManage.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiddlewareListResponse {
    private String name;
    private String version;
    private String type;
    private String path;
}
