package org.example.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.dto.ObservationDto;
import org.example.project.entity.PlantObservation;
import org.example.project.entity.Shift;
import org.example.project.repository.PlantObservationRepository;
import org.example.project.repository.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantObservationService {

    private final PlantObservationRepository observationRepository;
    private final ShiftRepository shiftRepository;

    @Transactional
    public PlantObservation recordObservation(ObservationDto dto, String employeeEmail) {
        log.info("Recording observation for employee: {} at {}", employeeEmail, dto.getDiscoveryTime());

        Shift activeShift = shiftRepository.findActiveByEmailAndTime(employeeEmail, dto.getDiscoveryTime())
                .orElseThrow(() -> new EntityNotFoundException("Активное дежурство не найдено для " + employeeEmail));

        if (dto.getDiscoveryTime().isBefore(activeShift.getStartTime()) ||
                dto.getDiscoveryTime().isAfter(activeShift.getEndTime())) {
            throw new IllegalArgumentException("Время наблюдения выходит за рамки назначенного дежурства");
        }

        PlantObservation obs = PlantObservation.builder()
                .shift(activeShift)
                .species(dto.getSpecies())
                .criticality(dto.getCriticality())
                .area(dto.getArea())
                .discoveryTime(dto.getDiscoveryTime())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        return observationRepository.save(obs);
    }
}