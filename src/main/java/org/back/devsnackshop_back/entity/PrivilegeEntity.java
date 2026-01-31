package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "privileges")
public class PrivilegeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_ko")
    private String nameKo;
}
