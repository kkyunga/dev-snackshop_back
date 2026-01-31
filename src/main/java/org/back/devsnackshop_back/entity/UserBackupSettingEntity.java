package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "user_backup_settings")
public class UserBackupSettingEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "backup_item_id")
    private Long backupItemId;

    @Column(name = "backup_interval")
    private Integer backupInterval;

    @Column(name = "storage_type")
    private String storageType;

    @Column(name = "storage_path")
    private String storagePath;

    @Column(name = "s3_bucket_name")
    private String s3BucketName;

    @Column(name = "region")
    private String region;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
