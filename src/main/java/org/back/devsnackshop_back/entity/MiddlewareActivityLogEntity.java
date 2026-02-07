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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installed_middleware_id")
    //
    private InstalledMiddlewareEntity installedMiddlewareId;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
