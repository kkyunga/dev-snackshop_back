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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "os_id")
    private Long osId;

    @Column(name = "cloud_id")
    private Long cloudId;

    @Column(name = "connection_method_id")
    private Long connectionMethodId;

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
