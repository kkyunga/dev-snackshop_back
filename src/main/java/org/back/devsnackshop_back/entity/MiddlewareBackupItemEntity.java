package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "middleware_backup_items")
// 미들웨어별 백업 항목 목록
public class MiddlewareBackupItemEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 미들웨어 종류
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "middleware_id")
    private MiddlewareEntity middlewareId;

    // 백업 항목 영문명(error, general, connection 등)
    @Column(name = "item_name_en")
    private String itemNameEn;

    // 백업 항목 한글명(에러로그, 일반로그, 접속로그 등)
    @Column(name = "item_name_ko")
    private String itemNameKo;
}
