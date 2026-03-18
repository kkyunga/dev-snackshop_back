package org.back.devsnackshop_back.dto.middlewareManage.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class MiddlewareListResponse {
    private String name;
    private String category;
    private List<String> versions;
    private String defaultPath;
    private Long defaultPort;
}
