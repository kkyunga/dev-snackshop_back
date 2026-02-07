package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface MiddlewareActivityLogRepository extends JpaRepository<MiddlewareActivityLogEntity, Long> {
    List<MiddlewareActivityLogEntity> findByInstalledMiddlewareIdOrderByCreatedAtDesc(InstalledMiddlewareEntity installedId);
}
