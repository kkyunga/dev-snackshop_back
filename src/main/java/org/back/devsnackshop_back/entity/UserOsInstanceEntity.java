package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "user_os_instances")
public class UserOsInstanceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id")
    private OsDistributionsEntity osId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloud_id")
    private CloudEntity cloudId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_method_id")
    private ConnectionMethodEntity connectionMethodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_id")
    private PrivilegeEntity privilegeId;

    @Column(name = "alias")
    private String alias;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "port_number")
    private Long portNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "auth_key_filename")
    private String authKeyFilename;

    @Column(name = "cpu_model_name")
    private String cpuModelName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
