package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;
//권한
@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "privileges")
// 서버 접근 권한 목록
public class PrivilegeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 권한 영문명(root, general 등)
    @Column(name = "name_en")
    private String nameEn;

    // 권한 한글명(관리자, 일반사용자 등)
    @Column(name = "name_ko")
    private String nameKo;
}
