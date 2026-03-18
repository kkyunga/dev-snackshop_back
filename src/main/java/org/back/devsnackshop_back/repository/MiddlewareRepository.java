package org.back.devsnackshop_back.repository;

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

    @Query(value = "SELECT t.id, t.middleware_name, t.middleware_type, t.version, t.version_order, t.is_simple_install, t.default_path, t.default_port " +
            "FROM (" +
            "    SELECT *, " +
            "    ROW_NUMBER() OVER (PARTITION BY middleware_name ORDER BY version_order DESC) as rn " +
            "    FROM middlewares" +
            ") t " +
            "WHERE t.rn = 1", nativeQuery = true)
    List<MiddlewareEntity> findByTopVersionOrder();
}
