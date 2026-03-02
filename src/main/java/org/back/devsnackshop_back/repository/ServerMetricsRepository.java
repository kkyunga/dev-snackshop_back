package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.elastic.ServerMetricsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerMetricsRepository extends ElasticsearchRepository<ServerMetricsDocument, String> {
    List<ServerMetricsDocument> findByServerIdOrderByTimestampAsc(Long serverId);
    Optional<ServerMetricsDocument> findFirstByServerIdOrderByTimestampDesc(Long serverId);
}
