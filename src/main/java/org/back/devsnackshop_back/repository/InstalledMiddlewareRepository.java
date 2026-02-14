package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface InstalledMiddlewareRepository extends JpaRepository<InstalledMiddlewareEntity, Long> {
    List<InstalledMiddlewareEntity> findByUserOsId(UserOsInstanceEntity userOsId);
}
