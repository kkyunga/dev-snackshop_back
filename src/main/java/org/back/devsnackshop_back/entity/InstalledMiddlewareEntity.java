package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "installed_middlewares")
// 사용자별 운영체제에 설치된 미들웨어 목록
public class InstalledMiddlewareEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 사용자의 운영체제 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_os_id")
    private UserOsInstanceEntity userOsId;

    // 미들웨어 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "middleware_id")
    private MiddlewareEntity middlewareId;

    // 설치 경로
    @Column(name = "install_path")
    private String installPath;

    // 포트 번호
    @Column(name = "port_number")
    private Long portNumber;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
