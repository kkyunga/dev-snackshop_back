package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {}
