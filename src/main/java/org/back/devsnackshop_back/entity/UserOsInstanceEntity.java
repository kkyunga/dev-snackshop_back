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
    private UserEntity userId;

    //서버 별명
    @Column(name = "alias")
    private String alias;

    //클라우드 서비스
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloud_id")
    private CloudEntity cloudId;

    //서버 종류
    @Column(name="server_type")
    private String serverOsType;
    
    //OS 버전
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id")
    private OsDistributionsEntity osId;

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


//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "connection_method_id")
//    private ConnectionMethodEntity connectionMethodId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "privilege_id")
//    private PrivilegeEntity privilegeId;
//
     // 접속 정보의 비밀번호
    @Column(name = "password")
    private String password;

    // 접속 정보의 인증키 파일명
    @Column(name = "auth_key_filename")
    private String authKeyFilename;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
