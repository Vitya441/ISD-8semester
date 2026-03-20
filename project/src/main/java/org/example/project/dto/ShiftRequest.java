package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftRequest {

    private Long employeeId;

    private Long zoneId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
