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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backup_setting_id")
    // 데이터 백업 설정 ID
    private UserBackupSettingEntity backupSettingId;

    @Column(name = "content")
    // 백업 상세 내용
    private String content;

    @Column(name = "file_name")
    // 생성된 백업 파일명
    private String fileName;

    @Column(name = "file_size")
    // 파일 크기
    private Integer fileSize;

    @Column(name = "file_path")
    // 파일 저장경로
    private String filePath;

    @Column(name = "is_success")
    // 성공여부(Y/N)
    private String isSuccess;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
