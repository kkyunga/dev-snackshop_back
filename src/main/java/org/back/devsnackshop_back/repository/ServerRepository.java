package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<ServerEntity,Long> {
}
