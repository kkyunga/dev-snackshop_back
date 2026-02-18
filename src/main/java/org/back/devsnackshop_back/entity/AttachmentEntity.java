package org.back.devsnackshop_back.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "attachments")
public class AttachmentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originFileName; // 사용자가 올린 원래 이름 (예: my_key.pem)

    @Column(nullable = false)
    private String storedFileName; // 서버에 저장된 이름 (UUID 포함)

    @Column(nullable = false)
    private String filePath;       // 저장된 경로

    private Long fileSize;         // 파일 크기 (bytes)

    private String fileType;       // 확장자 또는 MIME 타입

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
