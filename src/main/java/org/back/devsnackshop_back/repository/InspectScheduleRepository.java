package org.back.devsnackshop_back.repository;

import org.back.devsnackshop_back.entity.InspectSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectScheduleRepository extends JpaRepository<InspectSchedule, Long> {
    List<InspectSchedule> findByServerIdOrderByStartAtAsc(Long serverId);
}
