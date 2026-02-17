package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "notifications")
// 알림 로그
public class NotificationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 알림 제목
    @Column(name = "title")
    private String title;

    // 알림 내용
    @Column(name = "content")
    private String content;

    // 알림 발생일시
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}