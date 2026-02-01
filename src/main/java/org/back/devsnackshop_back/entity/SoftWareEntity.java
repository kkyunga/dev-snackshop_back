package org.back.devsnackshop_back.entity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "softwares")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SoftWareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String path;
    private String type; // 'auto', 'manual' 등

    /**
     * 핵심: 객체 참조(@ManyToOne) 대신 직접 ID값을 저장합니다.
     */
    @Column(name = "server_id")
    private Long serverId;
}