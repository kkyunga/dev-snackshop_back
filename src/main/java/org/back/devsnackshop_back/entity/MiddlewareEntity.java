package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "middlewares")
public class MiddlewareEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "middleware_type")
    private String middlewareType;

    @Column(name = "middleware_name")
    private String middlewareName;

    @Column(name = "version")
    private String version;
}
