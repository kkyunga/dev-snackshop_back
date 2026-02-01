package org.back.devsnackshop_back.dto.serververManage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor // JSON 파싱을 위해 기본 생성자 필수
public class ServerCreateRequest {
    private String label;
    private String ip;
    private int port;
    private String os;
    private String country;
    private String cloudService;
    private String authType;
    private String username;
    private String password;
    private List<SoftwareDto> softwareToInstall = new ArrayList<>();

    @Getter
    @ToString
    public static class SoftwareDto {
        private String name;
        private String path;
        private String type; // 👈 'auto' 또는 'manual' 등이 들어올 자리
    }
}