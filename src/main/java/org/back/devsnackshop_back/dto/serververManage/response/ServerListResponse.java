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
    private String cloudService;
    private String country;
    private String ip;
    private long port;
    private String os;
    private List<String> middlewares;
}
