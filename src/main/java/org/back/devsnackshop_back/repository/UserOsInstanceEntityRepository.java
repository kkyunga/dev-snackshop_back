package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface UserOsInstanceEntityRepository extends JpaRepository<UserOsInstanceEntity, Long> {
    List<UserOsInstanceEntity> findByUserId(Long userId);
}
