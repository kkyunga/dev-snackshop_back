package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface MiddlewareActivityLogRepository extends JpaRepository<MiddlewareActivityLogEntity, Long> {
    List<MiddlewareActivityLogEntity> findByInstalledMiddlewareIdOrderByCreatedAtDesc(InstalledMiddlewareEntity installedId);
    Optional<MiddlewareActivityLogEntity> findFirstByInstalledMiddlewareIdOrderByCreatedAtDesc(InstalledMiddlewareEntity installedId);
}
