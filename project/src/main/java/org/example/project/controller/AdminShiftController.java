package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ShiftRequest;
import org.example.project.entity.Shift;
import org.example.project.entity.Zone;
import org.example.project.service.ShiftService;
import org.example.project.service.ZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class AdminShiftController {

    private final ShiftService shiftService;
    private final ZoneService zoneService;

    @PostMapping("/zones")
    public ResponseEntity<Zone> createZone(@RequestBody Zone zone) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zoneService.saveZone(zone));
    }

    @PostMapping("/shifts")
    public ResponseEntity<Shift> scheduleShift(@RequestBody ShiftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShift(request));
    }
}
