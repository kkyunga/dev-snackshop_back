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
}
