package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.CloudFirewallRuleEntity;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.entity.elastic.LogAnalyzeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogAnalyzeRepository extends ElasticsearchRepository<LogAnalyzeDocument, String> {

    Optional<LogAnalyzeDocument> findFirstByServerIdOrderByCollectedAtDesc(long serverId);
    List<LogAnalyzeDocument> findTop10ByServerIdOrderByCollectedAtDesc(Long serverId);

    List<LogAnalyzeDocument> findByServerIdAndCollectedAtAfterOrderByCollectedAtDesc(Long serverId, LocalDateTime since);

    List<LogAnalyzeDocument> findTop200ByServerIdOrderByCollectedAtDesc(Long serverId);

}
