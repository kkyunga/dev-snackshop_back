package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "cloud_firewall_rules")
public class CloudFirewallRuleEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_os_instance_id")
    private Long userOsInstanceId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "port_number")
    private Long portNumber;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
