package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "installed_middlewares")
public class InstalledMiddlewareEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "os_id")
    private Long osId;

    @Column(name = "middleware_id")
    private Long middlewareId;

    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "install_path")
    private String installPath;

    @Column(name = "port_number")
    private Long portNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
