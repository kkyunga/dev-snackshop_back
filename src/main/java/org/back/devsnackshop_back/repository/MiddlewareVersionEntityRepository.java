package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface MiddlewareVersionEntityRepository extends JpaRepository<MiddlewareVersionEntity, Long> {
    List<MiddlewareVersionEntity> findByMiddlewareId(Long middlewareId);
}
