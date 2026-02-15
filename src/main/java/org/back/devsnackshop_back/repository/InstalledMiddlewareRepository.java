package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface InstalledMiddlewareRepository extends JpaRepository<InstalledMiddlewareEntity, Long> {
    @Query("SELECT im FROM InstalledMiddlewareEntity im " +
            "JOIN FETCH im.middlewareId " + // 미들웨어 정보를 미리 조인해서 가져옴
            "WHERE im.userOsId.id = :userOsId")
    List<InstalledMiddlewareEntity> findAllByUserOsId(@Param("userOsId") Long userOsId);
//    List<InstalledMiddlewareEntity> findByUserOsId(UserOsInstanceEntity userOsId);
}
