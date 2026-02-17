package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "middleware_activity_logs")
// 미들웨어 활동 로그
public class MiddlewareActivityLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 사용자별 설치된 미들웨어 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installed_middleware_id")
    private InstalledMiddlewareEntity installedMiddlewareId;

    // 활동상태(설치성공, 설치실패, 접속성공, 접속실패, 삭제, 업데이트 등)
    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
