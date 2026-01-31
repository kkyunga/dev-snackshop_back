package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "user_login_logs")
public class UserLoginLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 식별을 위해 ID 추가
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_login")
    private String isLogin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
