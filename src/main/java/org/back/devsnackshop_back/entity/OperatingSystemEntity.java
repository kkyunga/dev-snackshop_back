package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "operating_systems")
// 운영체제(서버) 대분류
public class OperatingSystemEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 서버명
    @Column(name = "os_type_name")
    private String osTypeName;
}