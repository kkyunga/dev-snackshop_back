package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password_encrypted")
    private String passwordEncrypted;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;




    // 2. 데이터 저장 전 자동으로 시간 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 3. 데이터 수정 시 자동으로 시간 갱신
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
