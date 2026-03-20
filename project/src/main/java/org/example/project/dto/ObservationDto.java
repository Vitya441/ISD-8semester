package org.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.project.entity.enums.Criticality;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObservationDto {

    private String species;

    private Criticality criticality;

    private Double area;

    private LocalDateTime discoveryTime;

    private Double latitude;

    private Double longitude;
}
