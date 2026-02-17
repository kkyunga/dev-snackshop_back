package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "user_backup_settings")
// 사용자별 데이터 백업 설정 목록
public class UserBackupSettingEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 사용자 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    // 미들웨어 백업 항목 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backup_item_id")
    private MiddlewareBackupItemEntity backupItemId;

    // 백업 주기
    @Column(name = "backup_interval")
    private Integer backupInterval;

    // 백업 파일 저장 위치 종류(local, S3 등)
    @Column(name = "storage_type")
    private String storageType;

    // 백업 파일 저장 경로
    @Column(name = "storage_path")
    private String storagePath;

    // S3 버킷명
    @Column(name = "s3_bucket_name")
    private String s3BucketName;

    // 나라위치
    @Column(name = "region")
    private String region;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
