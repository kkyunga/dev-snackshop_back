package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "os_access_logs")
public class OsAccessLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_os_instance_id")
    private Long userOsInstanceId;

    @Column(name = "is_success")
    private String isSuccess;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
