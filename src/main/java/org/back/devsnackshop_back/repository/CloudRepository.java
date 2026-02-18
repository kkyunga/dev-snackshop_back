package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.Optional;

@Repository
public interface CloudRepository extends JpaRepository<CloudEntity, Long> {
//    Optional<Object> findByCloudName(String cloudService);
}
