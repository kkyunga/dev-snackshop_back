package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
        import lombok.*;

        import java.util.List;

@Entity
@Table(name = "servers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@AllArgsConstructor
@Builder
public class ServerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private String ip;
    private int port;
    private String os;
    private String country;

    @Column(name = "cloud_service")
    private String cloudService;

    @Column(name = "auth_type")
    private String authType;

    private String username;
    private String password;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoftWareEntity> softWareEntityList;



}