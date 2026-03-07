package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.dto.inspectSchedule.InspectScheduleRequest;
import org.back.devsnackshop_back.dto.inspectSchedule.InspectScheduleResponse;
import org.back.devsnackshop_back.service.InspectScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inspectSchedules")

public class InspectScheduelController {


    private final InspectScheduleService inspectScheduleService;

    @GetMapping("/server/{serverId}")
    public ResponseEntity<List<InspectScheduleResponse>> getByServer(@PathVariable Long serverId) {
        return ResponseEntity.ok(inspectScheduleService.getByServer(serverId));
    }

    @PostMapping
    public ResponseEntity<InspectScheduleResponse> create(@RequestBody  InspectScheduleRequest req) {
        return ResponseEntity.ok(inspectScheduleService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspectScheduleResponse> update(
            @PathVariable Long id, @RequestBody  InspectScheduleRequest req) {
        return ResponseEntity.ok(inspectScheduleService.update(id, req));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InspectScheduleResponse> updateStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(inspectScheduleService.updateStatus(id, body.get("status")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inspectScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
