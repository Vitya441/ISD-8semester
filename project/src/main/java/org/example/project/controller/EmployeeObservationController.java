package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.ObservationDto;
import org.example.project.entity.PlantObservation;
import org.example.project.entity.Shift;
import org.example.project.service.PlantObservationService;
import org.example.project.service.ShiftService;
import org.example.project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeObservationController {

    private final PlantObservationService observationService;
    private final ShiftService shiftService;
    private final UserService userService;

    @PostMapping("/observation")
    public ResponseEntity<PlantObservation> recordObservation(
            @RequestBody ObservationDto dto,
            Principal principal) {
        return ResponseEntity.ok(observationService.recordObservation(dto, principal.getName()));
    }

    @GetMapping("/my-shifts")
    public ResponseEntity<List<Shift>> getMyShifts(Principal principal) {
        Long employeeId = userService.getByUsername(principal.getName()).getId();
        return ResponseEntity.ok(shiftService.findShiftsByEmployee(employeeId));
    }
}