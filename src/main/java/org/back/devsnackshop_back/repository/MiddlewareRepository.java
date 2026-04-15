package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.dto.middlewareManage.response.MiddlewareListResponse;
import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface MiddlewareRepository extends JpaRepository<MiddlewareEntity, Long> {
    List<MiddlewareEntity> findByMiddlewareType(String type);
    MiddlewareEntity findByMiddlewareNameAndVersion(String name, String version);
    MiddlewareEntity findTopByMiddlewareNameOrderByVersionOrderDesc(String name);
    List<MiddlewareEntity> findByIsSimpleInstall(String isSimpleInstall);


    @Query(value = "SELECT \n" +
            "    MIN(id) AS id,\n" +
            "    middleware_name,\n" +                          // ← 별칭 제거
            "    middleware_type,\n" +                          // ← 별칭 제거
            "    STRING_AGG(version, ',' ORDER BY version_order ASC) AS version,\n" +
            "    MIN(version_order) AS version_order,\n" +      // ← snake_case
            "    'N' AS is_simple_install,\n" +                 // ← snake_case
            "    default_path,\n" +                             // ← 별칭 제거
            "    default_port\n" +                              // ← 별칭 제거
            "FROM middlewares\n" +
            "GROUP BY \n" +
            "    middleware_name, \n" +
            "    middleware_type, \n" +
            "    default_path, \n" +
            "    default_port", nativeQuery = true)
    List<MiddlewareEntity> findByMiddlewareList();
}
