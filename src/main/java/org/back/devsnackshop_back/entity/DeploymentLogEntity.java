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

    // 사용자 정보 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    // 배포방식
    @Column(name = "deploy_method")
    private String deployMethod;

    // 배포 내용
    @Column(name = "content")
    private String content;

    // 배포 성공여부
    @Column(name = "is_success")
    private String isSuccess;

    // 롤백 여부(Y/N)
    @Column(name = "is_rollback")
    private String isRollback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
