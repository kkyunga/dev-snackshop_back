package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "connection_methods")
// 운영체제 접속방식 목록
public class ConnectionMethodEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "method_name")
    // 접속 방식(SSH, RDP, HTTP 등)
    private String methodName;
}
