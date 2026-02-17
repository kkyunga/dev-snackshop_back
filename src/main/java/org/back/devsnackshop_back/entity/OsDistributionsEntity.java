package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "os_distributions")
// 운영체제(서버) 상세종류
public class OsDistributionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    // 운영체제 대분류 ID
    @ManyToOne
    @JoinColumn(name = "os_id")
    private OperatingSystemEntity osId;

    // 운영체제 상세명
    @Column(name = "distro_name")
    private String distroName;

    // 운영체제 버전
    @Column(name = "version")
    private String version;
}
