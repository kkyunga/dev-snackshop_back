package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "server_purposes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 서버 용도 목록
public class ServerPurposeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 용도 이름 (예: "DEVELOPMENT", "PRODUCTION", "STAGING", "DATABASE")
    @Column(name = "purpose_value", nullable = false, unique = true)
    private String serverPurposeValue;

    // 프론트엔드에 보여줄 한글/설명 (예: "개발용", "운영용")
    @Column(name = "description")
    private String description;


}
