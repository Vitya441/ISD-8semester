package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.DailyReportDto;
import org.example.project.entity.PlantObservation;
import org.example.project.repository.PlantObservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PlantObservationRepository repository;

    public List<DailyReportDto> generateReport(LocalDate start, LocalDate end) {
        List<PlantObservation> observations = repository
                .findAllByDiscoveryTimeBetween(start.atStartOfDay(), end.atTime(23, 59, 59));

        Map<LocalDate, List<PlantObservation>> observationsByDate = observations.stream()
                .collect(Collectors.groupingBy(o -> o.getDiscoveryTime().toLocalDate()));

        return start.datesUntil(end.plusDays(1))
                .map(date -> buildDailyDto(date, observationsByDate.getOrDefault(date, List.of())))
                .collect(Collectors.toList());
    }

    private DailyReportDto buildDailyDto(LocalDate date, List<PlantObservation> dayObservations) {
        DailyReportDto dto = new DailyReportDto(date);

        dayObservations.forEach(obs -> {
            int slot = calculateTimeSlot(obs.getDiscoveryTime().getHour());
            if (slot != -1) {
                dto.addArea(slot, obs.getCriticality(), obs.getArea());
            }
        });

        return dto;
    }

    private int calculateTimeSlot(int hour) {
        if (hour >= 8 && hour < 12) return 1;
        if (hour >= 12 && hour < 16) return 2;
        if (hour >= 16 && hour < 20) return 3;
        return -1; // Вне интервалов мониторинга
    }
}