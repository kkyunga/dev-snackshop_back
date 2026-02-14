package org.back.devsnackshop_back.dto.serververManage.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerListResponse {
    private long id;
    private String label;
    private String cloud;
    private String country;
    private String ip;
    private long port;
    private String osName;
    private String osVersion;
    private List<MiddlewareData> middlewares;

    @Getter
    @Setter
    @ToString
    public static class MiddlewareData {
        private String middlewareType;
        private String middlewareName;
        private String middlewareVersion;
    }
}
