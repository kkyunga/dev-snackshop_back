package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder @Entity
@Table(name = "connection_methods")
public class ConnectionMethodEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "method_name")
    private String methodName;
}
