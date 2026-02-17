package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "data_backup_logs")
// 데이터 백업 실행 로그
public class DataBackupLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 데이터 백업 설정 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backup_setting_id")
    private UserBackupSettingEntity backupSettingId;

    // 백업 상세 내용
    @Column(name = "content")
    private String content;

    // 생성된 백업 파일명
    @Column(name = "file_name")
    private String fileName;

    // 파일 크기
    @Column(name = "file_size")
    private Integer fileSize;

    // 파일 저장경로
    @Column(name = "file_path")
    private String filePath;

    // 성공여부(Y/N)
    @Column(name = "is_success")
    private String isSuccess;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
