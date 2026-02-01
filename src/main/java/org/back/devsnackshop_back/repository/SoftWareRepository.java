package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.SoftWareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftWareRepository extends JpaRepository<SoftWareEntity,Long> {
}
