package org.back.devsnackshop_back.dto.inspectSchedule;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InspectScheduleRequest {
    @NotNull private Long serverId;
    @NotBlank
    private String title;
    private String description;
    @NotNull private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;
}
