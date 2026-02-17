package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "os_access_logs")
// 사용자별 운영체제 접속 로그
public class OsAccessLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 사용자의 운영체제 ID
    @ManyToOne
    @JoinColumn(name = "user_os_instance_id")
    private UserOsInstanceEntity userOsInstanceId;

    // 접속 여부(Y(성공)/N(실패))
    @Column(name = "is_success")
    private String isSuccess;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
