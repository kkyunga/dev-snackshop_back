package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.OsDistributionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OsDistributionsRepository extends JpaRepository<OsDistributionsEntity, Long> {

}
