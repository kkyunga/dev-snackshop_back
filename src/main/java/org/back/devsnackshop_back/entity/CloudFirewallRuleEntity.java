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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_os_instance_id")
    // 사용자별 운영체제(서버) 목록 ID
    private UserOsInstanceEntity userOsInstanceId;

    @Column(name = "service_name")
    // 서비스명(SSH, HTTP, HTTPS 등)
    private String serviceName;

    @Column(name = "port_number")
    // 포트번호
    private Long portNumber;

    @Column(name = "protocol")
    // 프로토콜
    private String protocol;

    @Column(name = "status")
    // 방화벽 상태
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
