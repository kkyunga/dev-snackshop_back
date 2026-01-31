package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "data_backup_logs")
public class DataBackupLogEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "backup_setting_id")
    private Long backupSettingId;

    @Column(name = "content")
    private String content;

    @Column(name = "filename")
    private String filename;

    @Column(name = "file_size")
    private Integer fileSize;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "is_success")
    private String isSuccess;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
