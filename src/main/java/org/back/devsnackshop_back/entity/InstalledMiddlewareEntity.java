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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    // 사용자 정보 ID
    private UserEntity userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id")
    // 운영체제 상세 배포 ID
    private OsDistributionsEntity osId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "middleware_id")
    // 미들웨어 ID
    private MiddlewareEntity middlewareId;

    @Column(name = "install_path")
    // 설치 경로
    private String installPath;

    @Column(name = "port_number")
    // 포트 번호
    private Long portNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
