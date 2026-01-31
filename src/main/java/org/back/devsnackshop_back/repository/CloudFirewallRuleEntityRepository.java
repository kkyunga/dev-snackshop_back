package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface CloudFirewallRuleEntityRepository extends JpaRepository<CloudFirewallRuleEntity, Long> {
    List<CloudFirewallRuleEntity> findByUserOsInstanceId(Long instanceId);
}
