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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="server_id")
    private ServerEntity serverEntity;




}