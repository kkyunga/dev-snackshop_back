package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "deployment_logs")
public class DeploymentLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "deploy_method")
    private String deployMethod;

    @Column(name = "content")
    private String content;

    @Column(name = "is_success")
    private String isSuccess;

    @Column(name = "is_rollback")
    private String isRollback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
