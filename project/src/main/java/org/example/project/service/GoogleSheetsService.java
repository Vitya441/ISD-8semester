package org.example.project.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.dto.ObservationDto;
import org.example.project.entity.enums.Criticality;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetsService {

    private final Sheets sheetsService;
    private final PlantObservationService observationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void syncObservations(String spreadsheetId, String range) throws IOException {
        log.info("Starting sync with Google Spreadsheet: {}", spreadsheetId);

        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.size() <= 1) {
            log.warn("No data found in spreadsheet range: {}", range);
            return;
        }

        for (int i = 1; i < values.size(); i++) {
            List<Object> row = values.get(i);

            try {
                if (row.isEmpty() || row.get(0).toString().isBlank()) continue;

                ObservationDto dto = mapRowToDto(row);

                String employeeEmail = row.get(1).toString();

                observationService.recordObservation(dto, employeeEmail);
                log.info("Successfully synced observation for species: {}", dto.getSpecies());

            } catch (Exception e) {
                log.error("Error parsing row {}: {}", i + 1, e.getMessage());
                // Продолжаем цикл, чтобы одна битая строка не ломала весь импорт
            }
        }
    }

    private ObservationDto mapRowToDto(List<Object> row) {
        ObservationDto dto = new ObservationDto();

        dto.setSpecies(row.get(2).toString());

        String critStatus = row.get(3).toString().trim().toUpperCase();
        dto.setCriticality(mapCriticality(critStatus));

        String areaStr = row.get(4).toString().replaceAll("[^\\d.]", "");
        dto.setArea(Double.valueOf(areaStr));

        dto.setDiscoveryTime(LocalDateTime.parse(row.get(0).toString().replace("T", " "), FORMATTER));

        dto.setLatitude(Double.valueOf(row.get(5).toString()));
        dto.setLongitude(Double.valueOf(row.get(6).toString()));

        return dto;
    }

    private Criticality mapCriticality(String status) {
        return switch (status) {
            case "ВЫСОКАЯ", "HIGH" -> Criticality.HIGH;
            case "СРЕДНЯЯ", "MEDIUM" -> Criticality.MEDIUM;
            default -> Criticality.LOW;
        };
    }
}
