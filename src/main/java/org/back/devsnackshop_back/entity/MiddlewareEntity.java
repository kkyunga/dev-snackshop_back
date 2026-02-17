package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "middlewares")
public class MiddlewareEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 미들웨어 종류(WEB, WAS, DBMS, Proxy 등)
    @Column(name = "middleware_type")
    private String middlewareType;

    // 미들웨어명
    @Column(name = "middleware_name")
    private String middlewareName;

    // 해당 미들웨어의 버전
    @Column(name = "version")
    private String version;

    // 버전의 최신 순서(숫자가 높을수록 최신)
    @Column(name = "version_order")
    private int versionOrder;
}
