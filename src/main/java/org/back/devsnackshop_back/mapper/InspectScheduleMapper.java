package org.back.devsnackshop_back.mapper;

import org.back.devsnackshop_back.dto.inspectSchedule.InspectScheduleResponse;
import org.back.devsnackshop_back.entity.InspectSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface InspectScheduleMapper {
    @Mapping(target = "status", expression = "java(schedule.getStatus().name())")
    InspectScheduleResponse toResponse(InspectSchedule schedule);

    List<InspectScheduleResponse> toResponseList(List<InspectSchedule> schedules);
}
