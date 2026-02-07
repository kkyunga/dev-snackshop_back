package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "clouds")
// 클라우드 목록
public class CloudEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cloud_type_name")
    // 클라우드 종류명(AWS, Azure, GCP 등)
    private String cloudTypeName;
}
