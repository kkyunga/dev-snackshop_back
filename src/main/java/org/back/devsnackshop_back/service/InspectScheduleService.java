package org.back.devsnackshop_back.service;

import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.dto.inspectSchedule.InspectScheduleRequest;
import org.back.devsnackshop_back.dto.inspectSchedule.InspectScheduleResponse;
import org.back.devsnackshop_back.entity.InspectSchedule;
import org.back.devsnackshop_back.enums.ScheduleStatus;
import org.back.devsnackshop_back.mapper.InspectScheduleMapper;
import org.back.devsnackshop_back.repository.InspectScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectScheduleService {
    private final InspectScheduleRepository inspectScheduleRepository;
    private final InspectScheduleMapper inspectScheduleMapper;





    public List<InspectScheduleResponse> getByServer(Long serverId) {
        return inspectScheduleMapper.toResponseList(
                inspectScheduleRepository.findByServerIdOrderByStartAtAsc(serverId));
    }

    public InspectScheduleResponse create(InspectScheduleRequest req) {
        InspectSchedule schedule = InspectSchedule.builder()
                .serverId(req.getServerId())
                .title(req.getTitle())
                .description(req.getDescription())
                .startAt(req.getStartAt())
                .endAt(req.getEndAt())
                .build();
        return inspectScheduleMapper.toResponse(inspectScheduleRepository.save(schedule));
    }

    public InspectScheduleResponse update(Long id, InspectScheduleRequest req) {
        InspectSchedule schedule = inspectScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));
        schedule.setTitle(req.getTitle());
        schedule.setDescription(req.getDescription());
        schedule.setStartAt(req.getStartAt());
        schedule.setEndAt(req.getEndAt());
        return inspectScheduleMapper.toResponse(inspectScheduleRepository.save(schedule));
    }

    public InspectScheduleResponse updateStatus(Long id, String status) {
        InspectSchedule schedule = inspectScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));
        schedule.setStatus(ScheduleStatus.valueOf(status));
        return inspectScheduleMapper.toResponse(inspectScheduleRepository.save(schedule));
    }

    public void delete(Long id) {
        inspectScheduleRepository.deleteById(id);
    }
}
