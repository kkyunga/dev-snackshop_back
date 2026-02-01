package org.back.devsnackshop_back.faker;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.back.devsnackshop_back.entity.*;
import org.back.devsnackshop_back.repository.*;
import org.back.devsnackshop_back.utils.PasswordUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Profile("local")
@Component
@RequiredArgsConstructor
public class DataFaker implements CommandLineRunner{
    private final Faker koFaker = new Faker(new Locale("ko"));
    private final Faker enFaker = new Faker(new Locale("en"));

    private final UserEntityRepository userEntityRepository;
    private final PrivilegeEntityRepository privilegeEntityRepository;
    private final CloudEntityRepository cloudEntityRepository;
    private final OperatingSystemEntityRepository operatingSystemEntityRepository;
    private final OsDistributionsRepository osDistributionsRepository;
    private final MiddlewareEntityRepository middlewareEntityRepository;

    @Override
    public void run(String... args) {
        if (userEntityRepository.count()<=0) userData();
        if (privilegeEntityRepository.count()<=0) privilegeData();
        if (cloudEntityRepository.count()<=0) cloudData();
        if (operatingSystemEntityRepository.count()<=0) operateSettingData();
        if (osDistributionsRepository.count()<=0) osDistributionData();
        if (middlewareEntityRepository.count()<=0) middlewareData();
    }

    private void userData() {
        String password = "postgre1234";

        ArrayList<UserEntity> result = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            String salt = enFaker.random().hex(16);
            String encryptedPassword = PasswordUtils.sha256(password, salt);

            UserEntity entity = UserEntity.builder()
                    .name(koFaker.name().name().replace(" ", ""))
                    .email(enFaker.internet().emailAddress())
                    .phoneNumber(String.format(
                            "010-%04d-%04d",
                            koFaker.number().numberBetween(0, 10000),
                            koFaker.number().numberBetween(0, 10000)
                    ))
                    .passwordEncrypted(encryptedPassword)
                    .salt(salt)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            result.add(entity);
        }
        userEntityRepository.saveAll(result);
    }

    private void privilegeData() {
        ArrayList<PrivilegeEntity> result = new ArrayList<>();
        PrivilegeEntity root = PrivilegeEntity.builder()
                .nameEn("root")
                .nameKo("관리자")
                .build();

        PrivilegeEntity general = PrivilegeEntity.builder()
                .nameEn("general")
                .nameKo("일반사용자")
                .build();

        result.add(root);
        result.add(general);

        privilegeEntityRepository.saveAll(result);
    }

    private void cloudData() {
        List<CloudEntity> result = new ArrayList<>();

        List<String> cloudProviders = List.of(
                "AWS",
                "Azure",
                "GCP",
                "OCI",
                "IBM Cloud",
                "Alibaba Cloud",
                "KT Cloud",
                "NHN Cloud",
                "Kakao i Cloud",
                "NCP"
        );

        cloudProviders.forEach(name -> {
            result.add(
                    CloudEntity.builder()
                            .cloudTypeName(name)
                            .build()
            );
        });

        cloudEntityRepository.saveAll(result);
    }


    private void operateSettingData() {
        ArrayList<OperatingSystemEntity> result = new ArrayList<>();

        List<String> operate = Arrays.asList(
                "LINUX",
                "WINDOWS",
                "MAC",
                "UNIX"
        );

        for (String str : operate) {
            OperatingSystemEntity entity = OperatingSystemEntity.builder()
                    .osTypeName(str)
                    .build();
            result.add(entity);
        }
        operatingSystemEntityRepository.saveAll(result);
    }

    private void osDistributionData() {
        List<OsDistributionsEntity> result = new ArrayList<>();

        // OS 그룹별 ID 매핑
        Map<String, Long> osIdMap = Map.of(
                "LINUX", 1L,
                "WINDOWS", 2L,
                "MAC", 3L
        );

        // 배포판 데이터
        Map<String, Map<String, List<String>>> distroMap = Map.of(
                "LINUX", Map.of(
                        "Ubuntu", List.of("18.04 LTS", "20.04 LTS", "22.04 LTS", "24.04 LTS"),
                        "Amazon Linux", List.of("2", "2023"),
                        "Debian", List.of("10 (Buster)", "11 (Bullseye)", "12 (Bookworm)"),
                        "Rocky Linux", List.of("8", "9"),
                        "AlmaLinux", List.of("8", "9"),
                        "RHEL", List.of("7", "8", "9")
                ),
                "WINDOWS", Map.of(
                        "Windows Server", List.of("2016", "2019", "2022"),
                        "Windows", List.of("10", "11")
                ),
                "MAC", Map.of(
                        "macOS", List.of("Monterey (12)", "Ventura (13)", "Sonoma (14)")
                )
        );

        // Entity 생성
        distroMap.forEach((osType, distros) -> {
            Long osId = osIdMap.get(osType);

            distros.forEach((distroName, versions) -> {
                versions.forEach(version -> {
                    result.add(
                            OsDistributionsEntity.builder()
                                    .osId(osId)
                                    .distroName(distroName)
                                    .version(version)
                                    .build()
                    );
                });
            });
        });

        osDistributionsRepository.saveAll(result);
    }


    private void middlewareData() {
        ArrayList<MiddlewareEntity> result = new ArrayList<>();

        Map<String, Map<String, List<String>>> middlewareMap = new HashMap<>();

        /* ===================== WEB ===================== */
        middlewareMap.put("WEB", Map.of(
                "Nginx", List.of(
                        "1.18.0", "1.20.2", "1.22.1", "1.24.0", "1.25.3"
                ),
                "Apache", List.of(
                        "2.4.41", "2.4.46", "2.4.52", "2.4.57", "2.4.58"
                ),
                "Caddy", List.of(
                        "2.4.6", "2.5.2", "2.6.4", "2.7.5", "2.8.4"
                ),
                "IIS", List.of(
                        "8.0", "8.5", "10.0",
                        "10.0 (Windows Server 2019)",
                        "10.0 (Windows Server 2022)"
                )
        ));

        /* ===================== WAS ===================== */
        middlewareMap.put("WAS", Map.of(
                "Tomcat", List.of(
                        "8.5.75", "9.0.65", "9.0.85", "10.0.27", "10.1.18"
                ),
                "JBoss", List.of(
                        "14.0.1", "17.0.1", "20.0.1", "26.1.3", "28.0.1"
                ),
                "Jetty", List.of(
                        "9.4.48", "9.4.52", "10.0.18", "11.0.18", "12.0.4"
                ),
                "Express", List.of(
                        "4.16.4", "4.17.3", "4.18.2", "5.0.0-beta.1", "5.0.0-beta.3"
                ),
                "Gunicorn", List.of(
                        "19.9.0", "20.0.4", "20.1.0", "21.2.0", "22.0.0"
                ),
                "uWSGI", List.of(
                        "2.0.18", "2.0.19.1", "2.0.20", "2.0.21", "2.0.23"
                ),
                "PHP-FPM", List.of(
                        "7.4", "8.0", "8.1", "8.2", "8.3"
                ),
                "Rudy", List.of(
                        "1.0.0", "1.1.0", "1.2.0", "1.3.0"
                )
        ));

        /* ===================== DBMS ===================== */
        middlewareMap.put("DBMS", Map.of(
                "MySQL", List.of(
                        "5.7", "8.0.28", "8.0.32", "8.0.34", "8.0.36"
                ),
                "PostgreSQL", List.of(
                        "11", "12", "13", "14", "15"
                ),
                "MariaDB", List.of(
                        "10.5", "10.6", "10.9", "10.11", "11.2"
                ),
                "Oracle", List.of(
                        "11g", "12c", "18c", "19c", "21c"
                ),
                "MongoDB", List.of(
                        "4.2", "4.4", "5.0", "6.0", "7.0"
                ),
                "Cassandra", List.of(
                        "3.11", "4.0", "4.1", "4.1.3", "5.0"
                )
        ));

        /* ===================== CACHE ===================== */
        middlewareMap.put("Cache", Map.of(
                "Redis", List.of(
                        "5.0", "6.0", "6.2", "7.0", "7.2"
                ),
                "Hazelcast", List.of(
                        "3.12", "4.2", "5.1", "5.3", "5.4"
                )
        ));

        /* ===================== MESSAGE QUEUE ===================== */
        middlewareMap.put("MQ", Map.of(
                "RabbitMQ", List.of(
                        "3.8", "3.9", "3.10", "3.11", "3.12"
                ),
                "ActiveMQ", List.of(
                        "5.15", "5.16", "5.17", "5.18", "6.0"
                ),
                "ZeroMQ", List.of(
                        "4.2.5", "4.3.2", "4.3.4", "4.3.5", "4.3.6"
                )
        ));

        /* ===================== STREAMING ===================== */
        middlewareMap.put("Streaming", Map.of(
                "Kafka", List.of(
                        "2.8", "3.0", "3.4", "3.6", "3.7"
                )
        ));

        /* ===================== PROXY ===================== */
        middlewareMap.put("Proxy", Map.of(
                "HAProxy", List.of(
                        "2.2", "2.4", "2.6", "2.8", "2.9"
                ),
                "LVS", List.of(
                        "1.2", "1.3", "1.4", "1.5"
                )
        ));


        middlewareMap.forEach((type, middlewares) -> {
            middlewares.forEach((name, versions) -> {
                versions.forEach(version -> {
                    result.add(
                            MiddlewareEntity.builder()
                                    .middlewareType(type)
                                    .middlewareName(name)
                                    .version(version)
                                    .build()
                    );
                });
            });
        });

        middlewareEntityRepository.saveAll(result);
    }
}
