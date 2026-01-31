package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "middleware_backup_items")
public class MiddlewareBackupItemEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "middleware_id")
    private Long middlewareId;

    @Column(name = "item_name_en")
    private String itemNameEn;

    @Column(name = "item_name_ko")
    private String itemNameKo;
}
