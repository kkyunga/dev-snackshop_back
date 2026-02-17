package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "cloud_firewall_rules")
// 사용자별 클라우드 방화벽 규칙 설정
public class CloudFirewallRuleEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 사용자별 운영체제(서버) 목록 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_os_instance_id")
    private UserOsInstanceEntity userOsInstanceId;

    // 서비스명(SSH, HTTP, HTTPS 등)
    @Column(name = "service_name")
    private String serviceName;

    // 포트번호
    @Column(name = "port_number")
    private Long portNumber;

    // 프로토콜
    @Column(name = "protocol")
    private String protocol;

    // 방화벽 상태
    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
