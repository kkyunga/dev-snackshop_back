package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "user_login_logs")
// 사용자 접속 로그
public class UserLoginLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 식별을 위해 ID 추가
    private Long id;

    // 사용자 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    // 로그인 여부(Y(로그인)/N(로그아웃))
    @Column(name = "is_login")
    private String isLogin;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
