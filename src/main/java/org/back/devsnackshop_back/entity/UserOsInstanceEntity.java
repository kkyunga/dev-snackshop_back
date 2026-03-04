package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "user_os_instances")
public class UserOsInstanceEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    // 사용자 ID
    private UserEntity user;

    //서버 별명
    @Column(name = "alias")
    private String alias;

    //클라우드 서비스
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloud_id")
    private CloudEntity cloud;

    //서버 종류
    @Column(name="server_type")
    private String serverOsType;
    
    //OS 버전
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id")
    private OsDistributionsEntity os;

    // IP 주소
    @Column(name = "ip_address")
    private String ipAddress;

    // port 번호
    @Column(name = "port_number")
    private Long portNumber;

    // 국가
    @Column(name = "country")
    private String country;

    // 접속 정보의 사용자명
    @Column(name = "username")
    private String username;



//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "privilege_id")
//    private PrivilegeEntity privilegeId;
//
     // 접속 정보의 비밀번호
    @Column(name = "password")
    private String password;

    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Update(수정) 전 실행
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
