package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "deployment_logs")
// 사용자별 배포 로그
public class DeploymentLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    // 사용자 정보 ID
    private UserEntity userId;

    @Column(name = "deploy_method")
    // 배포방식
    private String deployMethod;

    @Column(name = "content")
    // 배포 내용
    private String content;

    @Column(name = "is_success")
    // 배포 성공여부
    private String isSuccess;

    @Column(name = "is_rollback")
    // 롤백 여부(Y/N)
    private String isRollback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
