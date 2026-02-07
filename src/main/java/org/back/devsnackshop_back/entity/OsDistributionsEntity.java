package org.back.devsnackshop_back.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "os_distributions")
public class OsDistributionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "os_id")
    private OperatingSystemEntity osId;

    @Column(name = "distro_name")
    private String distroName;

    @Column(name = "version")
    private String version;
}
