package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface OperatingSystemRepository extends JpaRepository<OperatingSystemEntity, Long> {}
